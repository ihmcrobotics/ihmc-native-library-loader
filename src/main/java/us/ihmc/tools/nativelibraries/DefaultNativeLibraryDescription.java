package us.ihmc.tools.nativelibraries;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Architecture;
import us.ihmc.tools.nativelibraries.NativeLibraryDescription.OperatingSystem;

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
   public String getPackage(OperatingSystem operatingSystem, Architecture arch)
   {
      return packageName;
   }

   @Override   
   public NativeLibraryWithDependencies[] getLibrariesWithDependencies(OperatingSystem platform, Architecture arch)
   {
      return new NativeLibraryWithDependencies[] { NativeLibraryWithDependencies.fromPlatform(platform, arch, libraryName) };
   }

}
