
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("minikinpatch")
enable_language(ASM)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

find_package(bytehook REQUIRED CONFIG)

file(GLOB BYPASS Bypass/*.h Bypass/*.c)

if(ANDROID_ABI STREQUAL "armeabi-v7a")
    add_library(bypass STATIC ${BYPASS} Bypass/dlfcn_trampoline32.S)
elseif(ANDROID_ABI STREQUAL "arm64-v8a")
    add_library(bypass STATIC ${BYPASS} Bypass/dlfcn_trampoline64.S)
endif()

add_library( # Sets the name of the library.
             minikinpatch

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        minikinpatch.c
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( minikinpatch ${log-lib} bytehook::bytehook bypass)
