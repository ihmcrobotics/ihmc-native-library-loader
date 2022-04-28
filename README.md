#IHMC Native Library Loader

Simple helper library to load native libraries from a .jar bundle. Will extract the native library and load it from disk.

## Platform support

### Processor architectures
- x64 (also known as x86_64, AMD64)
- ARM64 (also known as AARCH64)

### Operating systems

- Windows 64 bit
- Linux 64 bit
- Mac OS X 64 bit

## Usage

NativeLibraryLoader.loadLibrary("package.path.to.library", "LibraryName");

The loader maps the name using the following rules:

Windows: [LibraryName][-arch].dll
Linux: lib[LibraryName][-arch].so
Mac OS X: lib[LibraryName][-arch].dylib

Where [-arch] is empty for x86 and `-arm64` for amd64.
