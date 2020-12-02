package us.ihmc.tools.nativelibraries;

public class NativeLibraryWithDependencies
{
   private final String libraryFilename;
   private final String dependencyFilenames[];

   private NativeLibraryWithDependencies(String libraryFilename, String... dependencyFilenames)
   {
      this.libraryFilename = libraryFilename;
      this.dependencyFilenames = dependencyFilenames;
   }

   public String getLibraryFilename()
   {
      return libraryFilename;
   }

   public String[] getDependencyFilenames()
   {
      return dependencyFilenames;
   }

   public static String getPlatformName(String libraryName)
   {
      return System.mapLibraryName(libraryName).replace(".jnilib", ".dylib");
   }

   /**
    * Create a library description with the library names based on the current platform
    * 
    * @param libraryName
    * @return
    */
   public static NativeLibraryWithDependencies fromPlatform(String libraryName, String... dependencies)
   {
      String[] platformDependencies;
      if(dependencies != null)
      {
         platformDependencies = new String[dependencies.length];
   
         for (int i = 0; i < dependencies.length; i++)
         {
            platformDependencies[i] = getPlatformName(dependencies[i]);
         }
      }
      else
      {
         platformDependencies = null;
      }

      return new NativeLibraryWithDependencies(getPlatformName(libraryName), platformDependencies);
   }

   /**
    * Create a library description with the raw library names
    * 
    * @param libraryFile
    * @param dependencyFiles
    * 
    * @return
    */
   public static NativeLibraryWithDependencies fromFilename(String libraryFile, String... dependencyFiles)
   {
      return new NativeLibraryWithDependencies(libraryFile, dependencyFiles);
   }

}
