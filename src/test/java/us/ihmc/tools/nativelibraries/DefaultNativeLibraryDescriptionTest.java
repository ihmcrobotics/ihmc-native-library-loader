package us.ihmc.tools.nativelibraries;

import org.apache.commons.lang3.SystemUtils;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Architecture;
import us.ihmc.tools.nativelibraries.NativeLibraryDescription.OperatingSystem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DefaultNativeLibraryDescriptionTest
{
   @Test
   public void testContructNativeLibraryDescription()
   {
      String packageName = "us.ihmc.native";
      String libraryName = "ihmcNativeUtils";
      String windowsCorrectName = libraryName + ".dll";
      String macCorrectName = "lib" + libraryName + ".dylib";
      String linuxCorrectName = "lib" + libraryName + ".so";
      DefaultNativeLibraryDescription defaultNativeLibraryDescription = new DefaultNativeLibraryDescription(packageName, libraryName);
      
      assertEquals(packageName, defaultNativeLibraryDescription.getPackage(), "Package name not set.");
      
      if (SystemUtils.IS_OS_WINDOWS)
      {
         String actualName = defaultNativeLibraryDescription.getLibrariesWithDependencies(OperatingSystem.WIN64, Architecture.x64)[0].getLibraryFilename();
         System.out.println(windowsCorrectName + " =? " + actualName);
         assertEquals(windowsCorrectName, actualName, "Library name not correct on Windows.");
      }
      else if (SystemUtils.IS_OS_MAC)
      {
         String actualName = defaultNativeLibraryDescription.getLibrariesWithDependencies(OperatingSystem.MACOSX64, Architecture.x64)[0].getLibraryFilename();
         System.out.println(macCorrectName + " =? " + actualName);
         assertEquals(macCorrectName, actualName, "Library name not correct on Mac.");
      }
      else if (SystemUtils.IS_OS_LINUX)
      {
         String actualName = defaultNativeLibraryDescription.getLibrariesWithDependencies(OperatingSystem.LINUX64, Architecture.x64)[0].getLibraryFilename();
         System.out.println(linuxCorrectName + " =? " + actualName);
         assertEquals(linuxCorrectName, actualName, "Library name not correct on Linux.");
      }
      else
      {
         fail("Unsupported OS.");
      }
   }
}
