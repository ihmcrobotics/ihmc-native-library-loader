package us.ihmc.tools.nativelibraries;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Architecture;
import us.ihmc.tools.nativelibraries.NativeLibraryDescription.OperatingSystem;

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
      
      
      
      
      
      {
         assertEquals(packageName, defaultNativeLibraryDescription.getPackage(OperatingSystem.WIN64, Architecture.x64), "Package name not set.");
         
         String actualName = defaultNativeLibraryDescription.getLibraryWithDependencies(OperatingSystem.WIN64, Architecture.x64).getLibraryFilename();
         System.out.println(windowsCorrectName + " =? " + actualName);
         assertEquals(windowsCorrectName, actualName, "Library name not correct on Windows.");
      }

      {
         assertEquals(packageName, defaultNativeLibraryDescription.getPackage(OperatingSystem.MACOSX64, Architecture.x64), "Package name not set.");

         String actualName = defaultNativeLibraryDescription.getLibraryWithDependencies(OperatingSystem.MACOSX64, Architecture.x64).getLibraryFilename();
         System.out.println(macCorrectName + " =? " + actualName);
         assertEquals(macCorrectName, actualName, "Library name not correct on Mac.");
      }

      {
         assertEquals(packageName, defaultNativeLibraryDescription.getPackage(OperatingSystem.LINUX64, Architecture.x64), "Package name not set.");

         
         String actualName = defaultNativeLibraryDescription.getLibraryWithDependencies(OperatingSystem.LINUX64, Architecture.x64).getLibraryFilename();
         System.out.println(linuxCorrectName + " =? " + actualName);
         assertEquals(linuxCorrectName, actualName, "Library name not correct on Linux.");
      }
   }
}
