package us.ihmc.tools.nativelibraries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.SystemUtils;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Platform;

/**
 * Helper class that unpacks and optionally loads native libraries
 * 
 * @author Jesper Smith
 *
 */
public class NativeLibraryLoader
{
   public final static String LIBRARY_LOCATION = new File(System.getProperty("user.home"), ".ihmc" + File.separator + "lib").getAbsolutePath();
   private static final HashMap<URL, String> extractedLibraries = new HashMap<>();
   private static final HashSet<String> loadedLibraries = new HashSet<>();

   private NativeLibraryLoader()
   {
      // Disallow construction
   }

   /** 
    * Convenience method to load a single library that's available on all platforms. Throws a UnsatisfiedLinkError if the library cannot be found.
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
      return extractLibraryAbsolute(packageName, System.mapLibraryName(libraryName));
   }

   /**
    * Extract multiple JNA libraries. Returns the full path to the containing directory. DirectoryName is based on the first library name.
    * @param packageName Name of the package
    * @param mainLibrary Name of the main library. A SHA-1 hash is taken of this library.
    * @param libraries Names of the libraries
    * 
    * @return Full path to the directory containing the libraries
    */
   public static String extractLibraries(String packageName, String mainLibrary, String... libraries)
   {
      File firstLibrary = new File(extractLibrary(packageName, mainLibrary));
      File containingDirectory = firstLibrary.getParentFile();

      String prefix = createPackagePrefix(packageName);
      for (String library : libraries)
      {
         String libraryMapped = System.mapLibraryName(library);
         File target = new File(containingDirectory, libraryMapped);
         if (!target.exists())
         {
            try
            {
               InputStream stream = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(prefix + libraryMapped);
               FileOutputStream out = new FileOutputStream(target);
               byte[] buf = new byte[1024];
               int len;
               while ((len = stream.read(buf)) > 0)
               {
                  out.write(buf, 0, len);
               }

               out.close();
            }
            catch (IOException e)
            {

            }
         }
      }
      
      return containingDirectory.getAbsolutePath();

   }

   public static String extractLibraryAbsolute(String packageName, String library)
   {
      String prefix = createPackagePrefix(packageName);
      URL libraryURL = NativeLibraryLoader.class.getClassLoader().getResource(prefix + library);

      if (libraryURL == null)
      {
         throw new UnsatisfiedLinkError("Cannot load library " + prefix + library);
      }

      if (extractedLibraries.containsKey(libraryURL))
      {
         return extractedLibraries.get(libraryURL);
      }

      // Try to load the library directly. If not possible, fall trough and unpack to temp directory
      if ("file".equals(libraryURL.getProtocol()))
      {
         try
         {
            File libraryFile = new File(libraryURL.toURI());
            if (libraryFile.canRead())
            {
               String absolutePath = libraryFile.getAbsolutePath();
               extractedLibraries.put(libraryURL, absolutePath);
               return absolutePath;
            }
         }
         catch (URISyntaxException e)
         {
         }
      }

      File directory = new File(LIBRARY_LOCATION + "/" + prefix);
      if (!directory.exists())
      {
         directory.mkdirs();
      }
      InputStream stream = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(prefix + library);
      if (stream == null)
      {
         throw new UnsatisfiedLinkError("Cannot load library " + prefix + library);
      }
      File lib = writeStreamToFile(stream, library, directory);

      try
      {
         stream.close();
      }
      catch (IOException e)
      {
      }
      String absolutePath = lib.getAbsolutePath();
      extractedLibraries.put(libraryURL, absolutePath);
      return absolutePath;
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
      if (SystemUtils.IS_OS_WINDOWS && isX86_64())
      {
         platform = Platform.WIN64;
      }
      else if (SystemUtils.IS_OS_MAC && isX86_64())
      {
         platform = Platform.MACOSX64;
      }
      else if (SystemUtils.IS_OS_LINUX && isX86_64())
      {
         platform = Platform.LINUX64;
      }
      else
      {
         System.err.println("Cannot load library. Platform not supported");
         return false;
      }

      String packageName = libraryDescription.getPackage();
      String[] libraries = libraryDescription.getLibraries(platform);
      if (libraries == null || libraries.length == 0)
      {
         return false;
      }

      try
      {
         for (String library : libraries)
         {
            loadLibraryFromClassPath(packageName, library);
         }
      }
      catch (UnsatisfiedLinkError e)
      {
         System.err.println(e.getMessage());
         return false;
      }

      return true;
   }

   private synchronized static void loadLibraryFromClassPath(String packageName, String library)
   {
      String identifier = packageName + "+" + library;
      if (!loadedLibraries.contains(identifier))
      {
         System.load(extractLibraryAbsolute(packageName, library));
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

   private static File writeStreamToFile(InputStream stream, String libraryName, File directory)
   {
      try
      {
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
         DigestInputStream digestStream = new DigestInputStream(stream, messageDigest);
         File file = File.createTempFile(".NativeLibraryLoader", "Tmp", directory);
         FileOutputStream out = new FileOutputStream(file);
         byte[] buf = new byte[1024];
         int len;
         while ((len = digestStream.read(buf)) > 0)
         {
            out.write(buf, 0, len);
         }

         out.close();

         String subdirectoryName = DatatypeConverter.printHexBinary(messageDigest.digest());
         File subDirectory = new File(directory, subdirectoryName);
         File target = new File(subDirectory, libraryName);

         if (!target.exists())
         {
            subDirectory.mkdirs();
            file.renameTo(target);
         }
         else
         {
            file.delete();
         }
         return target;

      }
      catch (IOException | NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }

   }

   private static boolean isX86_64()
   {
      return System.getProperty("os.arch").contains("64");
   }
}
