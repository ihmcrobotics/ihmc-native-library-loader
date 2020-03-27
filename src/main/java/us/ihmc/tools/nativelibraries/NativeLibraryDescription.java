package us.ihmc.tools.nativelibraries;

public interface NativeLibraryDescription
{
   public enum Platform
   {
      WIN32, WIN64, MACOSX64, LINUX32, LINUX64
   }

   /**
    * Get the package the native libraries are stored in.
    * 
    * @return
    */
   public String getPackage();

   /**
    * Get a list of libraries to load for current platform. Loading multiple libraries is especially
    * useful for Windows, as you don't have to muck around with RPATHS.
    * 
    * Deprecated: Use getLibrariesWithDependencies() instead
    * 
    * @param platform Get libraries for platform
    * @return List The libraries to load in order.
    */
   @Deprecated
   default public String[] getLibraries(Platform platform)
   {
      return new String[0];
   }
   
   
   /**
    * Get a list of libraries and their dependencies to load for current platform. 
    * 
    * On Windows, the dependencies are loaded in order. On other systems, you need to make sure to set the RPATH to $ORIGIN (Unix) or @rpath (MacOSX) to load the dependencies.
    * 
    * @param platform Get libraries for platform
    * @return List The libraries to load in order, with their dependencies
    */
   default NativeLibraryWithDependencies[] getLibrariesWithDependencies(Platform platform)
   {
      String[] libraryNames = getLibraries(platform);
      NativeLibraryWithDependencies[] libraries = new NativeLibraryWithDependencies[libraryNames.length];
      
      for(int i = 0; i < libraries.length; i++)
      {
         libraries[i] = NativeLibraryWithDependencies.fromFilename(libraryNames[i]);
      }
      
      return libraries;
      
   }
   
   
}
