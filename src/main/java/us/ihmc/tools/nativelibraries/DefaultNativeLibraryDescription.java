package us.ihmc.tools.nativelibraries;

public class DefaultNativeLibraryDescription implements NativeLibraryDescription
{
   private final String packageName;
   private final NativeLibraryWithDependencies[] libraries;

   public DefaultNativeLibraryDescription(String packageName, String libraryName)
   {
      this.packageName = packageName;
      this.libraries = new NativeLibraryWithDependencies[] { NativeLibraryWithDependencies.fromPlatform(libraryName) };
   }

   @Override
   public String getPackage()
   {
      return packageName;
   }

   @Override   
   public NativeLibraryWithDependencies[] getLibrariesWithDependencies(Platform platform)
   {
      return libraries;
   }

}
