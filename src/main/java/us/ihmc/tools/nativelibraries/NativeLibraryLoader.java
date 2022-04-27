package us.ihmc.tools.nativelibraries;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.arch.Processor;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Platform;

/**
 * Helper class that unpacks and optionally loads native libraries
 * 
 * @author Jesper Smith
 */
public class NativeLibraryLoader
{
   public final static String LIBRARY_LOCATION = new File(System.getProperty("user.home"), ".ihmc" + File.separator + "lib").getAbsolutePath();
   private static final HashMap<String, List<String>> extractedLibraries = new HashMap<>();
   private static final HashSet<String> loadedLibraries = new HashSet<>();

   private NativeLibraryLoader()
   {
      // Disallow construction
   }

   /**
    * Convenience method to load a single library that's available on all platforms. Throws a
    * UnsatisfiedLinkError if the library cannot be found.
    * 
    * @param packageName
    * @param libraryName
    */
   public static void loadLibrary(String packageName, String libraryName)
   {
      if (!loadLibrary(new DefaultNativeLibraryDescription(packageName, libraryName)))
      {
         throw new UnsatisfiedLinkError("Cannot load " + createPackagePrefix(packageName) + libraryName);
      }
   }

   /**
    * Extract the library if necessary, returns full path to the library. Useful for JNA libraries.
    * 
    * @param packageName Name of the package
    * @param libraryName Name of the library
    * @return Full path to the library.
    */
   public static String extractLibrary(String packageName, String libraryName)
   {
      return extractLibraryWithDependenciesAbsolute(packageName, NativeLibraryWithDependencies.fromPlatform(libraryName)).get(0);
   }

   /**
    * Extract multiple JNA libraries. Returns the full path to the containing directory. DirectoryName
    * is based on the first library name.
    * 
    * @param packageName Name of the package
    * @param mainLibrary Name of the main library. A SHA-1 hash is taken of this library.
    * @param libraries   Names of the libraries
    * @return Full path to the directory containing the libraries
    */
   public static String extractLibraries(String packageName, String mainLibrary, String... libraries)
   {

      NativeLibraryWithDependencies library = NativeLibraryWithDependencies.fromPlatform(mainLibrary, libraries);

      List<String> extracted = extractLibraryWithDependenciesAbsolute(packageName, library);

      return extracted.get(0);

   }
   
   @Deprecated
   public static String extractLibraryAbsolute(String packageName, String library)
   {
      NativeLibraryWithDependencies libraryWithDependencies = NativeLibraryWithDependencies.fromFilename(library);
      return extractLibraryWithDependenciesAbsolute(packageName, libraryWithDependencies).get(0);
   }

   public synchronized static List<String> extractLibraryWithDependenciesAbsolute(String packageName, NativeLibraryWithDependencies library)
   {

      try
      {
         
         String hash = getHash(packageName, library);
         
         if (extractedLibraries.containsKey(hash))
         {
            return extractedLibraries.get(hash);
         }
   
         String prefix = createPackagePrefix(packageName);
         File packageDirectory = new File(LIBRARY_LOCATION, prefix);
         File directory = new File(packageDirectory, hash);
         if (!directory.exists())
         {
            directory.mkdirs();
         }
   
         List<String> libraryFiles = new ArrayList<>();
   
         List<InputStream> inputStreams = getInputstreams(packageName, library);

         libraryFiles.add(extractFile(directory, library.getLibraryFilename(), inputStreams.get(0)));
   
         for (int i = 1; i < inputStreams.size(); i++)
         {
            libraryFiles.add(extractFile(directory, library.getDependencyFilenames()[i - 1], inputStreams.get(i)));
         }
         
         closeInputStreams(inputStreams);
   
         extractedLibraries.put(hash, libraryFiles);
         return libraryFiles;
      }
      catch(IOException | NoSuchAlgorithmException e)
      {
         throw new UnsatisfiedLinkError(e.getMessage());
      }
      
   }

   /**
    * Tries to load libraries in libraryDescription. Returns false if libraries cannot be loaded
    * 
    * @param libraryDescription
    * @return
    */
   public synchronized static boolean loadLibrary(NativeLibraryDescription libraryDescription)
   {
      Platform platform;
      if (SystemUtils.IS_OS_WINDOWS && isX86_32())
      {
         platform = Platform.WIN32;
      }
      else if (SystemUtils.IS_OS_WINDOWS && isX86_64())
      {
         platform = Platform.WIN64;
      }
      else if (SystemUtils.IS_OS_MAC && isX86_64())
      {
         platform = Platform.MACOSX64;
      }
      else if (SystemUtils.IS_OS_LINUX && isX86_32())
      {
         platform = Platform.LINUX32;
      }
      else if (SystemUtils.IS_OS_LINUX && (isX86_64() || isARM_64()))
      {
         platform = Platform.LINUX64;
      }
      else
      {
         System.err.println("Cannot load library. Platform not supported");
         return false;
      }

      String packageName = libraryDescription.getPackage();
      NativeLibraryWithDependencies[] libraries = libraryDescription.getLibrariesWithDependencies(platform);
      if (libraries == null || libraries.length == 0)
      {
         return false;
      }

      try
      {
         for (NativeLibraryWithDependencies library : libraries)
         {
            loadLibraryFromClassPath(platform, packageName, library);
         }
      }
      catch (UnsatisfiedLinkError e)
      {
         System.err.println(e.getMessage());
         return false;
      }

      return true;
   }

   private synchronized static void loadLibraryFromClassPath(Platform platform, String packageName, NativeLibraryWithDependencies library)
   {
      String identifier = packageName + "+" + library.getLibraryFilename();
      
            
      if (!loadedLibraries.contains(identifier))
      {
         List<String> libraries = extractLibraryWithDependenciesAbsolute(packageName, library);

         
         // On windows, load the dependencies before loading the actual plugin
         if(platform == Platform.WIN32 || platform == Platform.WIN64)
         {
            // Dependencies are libraries 1 - n. Load these first
            for(int i = 1; i < libraries.size(); i++)
            {
               System.load(libraries.get(i));
            }
         }
         
         System.load(libraries.get(0));
         loadedLibraries.add(identifier);
      }
   }

   private static String createPackagePrefix(String packageName)
   {
      packageName = packageName.trim().replace('.', '/');
      if (packageName.length() > 0)
      {
         packageName = packageName + '/';
      }
      return packageName;
   }

   private static String extractFile(File target, String name, InputStream inputStream) throws IOException
   {
      File targetFile = new File(target, name);
      if (!targetFile.exists())
      {
         Files.copy(inputStream, targetFile.toPath());
      }

      return targetFile.getAbsolutePath();
   }

   private static void closeInputStreams(List<InputStream> inputStreams)
   {
      for (InputStream is : inputStreams)
      {
         try
         {
            is.close();
         }
         catch (IOException e)
         {
         }
      }
   }

   private static List<InputStream> getInputstreams(String packageName, NativeLibraryWithDependencies nativeLibrary)
   {
      String prefix = createPackagePrefix(packageName);

      ArrayList<InputStream> inputStreams = new ArrayList<>();
      InputStream libraryInputStream = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(prefix + nativeLibrary.getLibraryFilename());

      if (libraryInputStream == null)
      {
         throw new UnsatisfiedLinkError("Cannot load library " + prefix + nativeLibrary.getLibraryFilename());
      }

      inputStreams.add(libraryInputStream);

      for (String dependency : nativeLibrary.getDependencyFilenames())
      {
         InputStream dependencyLibrary = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(prefix + dependency);

         if (dependencyLibrary == null)
         {
            throw new UnsatisfiedLinkError("Cannot load library " + prefix + dependency);
         }

         inputStreams.add(dependencyLibrary);
      }

      return inputStreams;

   }

   private static String getHash(String packageName, NativeLibraryWithDependencies nativeLibrary) throws IOException, NoSuchAlgorithmException
   {
      List<InputStream> inputStreams = getInputstreams(packageName, nativeLibrary);
      
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

      for (InputStream is : inputStreams)
      {
         DigestInputStream digestStream = new DigestInputStream(is, messageDigest);
         byte[] buf = new byte[1024];

         while (digestStream.read(buf) > 0)
            ;
      }

      closeInputStreams(inputStreams);
      return DatatypeConverter.printHexBinary(messageDigest.digest());

   }

   private static boolean isARM_64()
   {
	  return SystemUtils.OS_ARCH.equals("aarch64");
   }
   

   private static boolean isX86_32()
   {
      Processor processor = ArchUtils.getProcessor();
      if (processor != null) {
          return processor.isX86() && processor.is32Bit();
      } else {
    	  return false;
      }

   }

   private static boolean isX86_64()
   {
      Processor processor = ArchUtils.getProcessor();
      if (processor != null) {
    	  return processor.isX86() && processor.is64Bit();
      }
      else {
    	  return false;
      }
   }
}