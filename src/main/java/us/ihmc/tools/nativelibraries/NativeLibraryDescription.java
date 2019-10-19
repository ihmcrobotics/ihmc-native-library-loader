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
    * Get a list of libraries to load for current platform. Loading multiple libraries is especially useful for Windows, as you don't have to muck around with RPATHS.
    * 
    * @param platform Get libraries for platform
    * @return List The libraries to load in order.
    */
   public String[] getLibraries(Platform platform);
}
