#IHMC Native Library Loader

Simple helper library to load native libraries from a .jar bundle. Will extract the native library and load it from disk.

## Usage

NativeLibraryLoader.loadLibrary("package.path.to.library", "LibraryName");

The loader uses System.mapLibraryName() to create the platform specific library name. On Mac OS X, the library is expected to end with .dylib. 

## Multi-platform support

- x86-64 libraries use the base name for the library
- Other architectures use a separate identifier for the library name.
	- arm64: LibraryName becomes libLibraryName-arm64.so, LibraryName-arm64.dll etc