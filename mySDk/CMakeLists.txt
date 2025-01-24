# Minimum CMake version required
cmake_minimum_required(VERSION 3.4.1)

# Project name
project("You Matter")

# Include directories for header files (adjust the path as needed)
include_directories(${CMAKE_SOURCE_DIR}/path/to/your/headers)

# Specify the C++ standard and enabled languages
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_ANDROID_ARCH_ABI armeabi-v7a arm64-v8a x86 x86_64)

# Enable the use of assembly
enable_language(ASM)

# Add the shared library and its source files
add_library(
        native-lib
        SHARED
        src/main/java/com/test/my/app/cpp/syscall.S
        src/main/java/com/test/my/app/cpp/native-lib.cpp
)

# Find the log library (from the NDK)
find_library(
        log-lib
        log
)

# Link libraries for native-lib (adding atomic and math libraries)
target_link_libraries(
        native-lib
        ${log-lib}
        atomic
        m
)

# Make sure that the missing functions `my_openat` and `my_read` are defined
# If these are custom functions, ensure they are implemented in the `native-lib.cpp` or another .cpp file
# Example:
#
# In `native-lib.cpp` or another C++ file:
# extern "C" {
#     int my_openat(int dirfd, const char* pathname, int flags);
#     ssize_t my_read(int fd, void* buf, size_t count);
# }
#
# Then implement these functions:
#
# int my_openat(int dirfd, const char* pathname, int flags) {
#     // Implementation of my_openat
#     return open(pathname, flags);  // Example of using `open`
# }
#
# ssize_t my_read(int fd, void* buf, size_t count) {
#     // Implementation of my_read
#     return read(fd, buf, count);  // Example of using `read`
# }
