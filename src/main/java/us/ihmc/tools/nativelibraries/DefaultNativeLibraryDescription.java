package us.ihmc.tools.nativelibraries;

public class DefaultNativeLibraryDescription implements NativeLibraryDescription
{
   private final String packageName;
   private final String[] libraryNames;
   
   public DefaultNativeLibraryDescription(String packageName, String libraryName)
   {
      this.packageName = packageName;
      this.libraryNames = new String[] { System.mapLibraryName(libraryName).replace(".jnilib", ".dylib") };
   }

   @Override
   public String getPackage()
   {
      return packageName;
   }

   @Override
   public String[] getLibraries(Platform platform)
   {
      return libraryNames;
   }
}
