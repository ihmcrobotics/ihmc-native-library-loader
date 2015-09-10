package us.ihmc.tools.nativelibraries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Platform;

public class DefaultNativeLibraryDescriptionTest
{
   @Test(timeout = 30000)
   public void testContructNativeLibraryDescription()
   {
      String packageName = "us.ihmc.native";
      String libraryName = "ihmcNativeUtils";
      String windowsCorrectName = libraryName + ".dll";
      String macCorrectName = "lib" + libraryName + ".dylib";
      String linuxCorrectName = "lib" + libraryName + ".so";
      DefaultNativeLibraryDescription defaultNativeLibraryDescription = new DefaultNativeLibraryDescription(packageName, libraryName);
      
      assertEquals("Package name not set.", packageName, defaultNativeLibraryDescription.getPackage());
      
      if (SystemUtils.IS_OS_WINDOWS)
      {
         String actualName = defaultNativeLibraryDescription.getLibraries(Platform.WIN64)[0];
         System.out.println(windowsCorrectName + " =? " + actualName);
         assertEquals("Library name not correct on Windows.", windowsCorrectName, actualName);
      }
      else if (SystemUtils.IS_OS_MAC)
      {
         String actualName = defaultNativeLibraryDescription.getLibraries(Platform.MACOSX64)[0];
         System.out.println(macCorrectName + " =? " + actualName);
         assertEquals("Library name not correct on Mac.", macCorrectName, actualName);
      }
      else if (SystemUtils.IS_OS_LINUX)
      {
         String actualName = defaultNativeLibraryDescription.getLibraries(Platform.LINUX64)[0];
         System.out.println(linuxCorrectName + " =? " + actualName);
         assertEquals("Library name not correct on Linux.", linuxCorrectName, actualName);
      }
      else
      {
         fail("Unsupported OS.");
      }
   }
}
