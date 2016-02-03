#IHMC Native Library Loader

Simple helper library to load native libraries from a .jar bundle. Will extract the native library and load it from disk.

## Usage

NativeLibraryLoader.loadLibrary("package.path.to.library", "LibraryName");

The loader uses System.mapLibraryName() to create the platform specific library name. On Mac OS X, the library is expected to end with .dylib. 
