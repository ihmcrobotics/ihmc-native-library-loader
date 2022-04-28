package us.ihmc.tools.nativelibraries;

public class DefaultNativeLibraryDescription implements NativeLibraryDescription
{
   private final String packageName;
   private final String libraryName;
      

   public DefaultNativeLibraryDescription(String packageName, String libraryName)
   {
      this.packageName = packageName;
      this.libraryName = libraryName;
   }

   @Override
   public String getPackage()
   {
      return packageName;
   }

   @Override   
   public NativeLibraryWithDependencies[] getLibrariesWithDependencies(OperatingSystem platform, Architecture arch)
   {
      return new NativeLibraryWithDependencies[] { NativeLibraryWithDependencies.fromPlatform(platform, arch, libraryName) };
   }

}
