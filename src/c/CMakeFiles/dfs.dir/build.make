# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.16

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /Applications/CMake.app/Contents/bin/cmake

# The command to remove a file.
RM = /Applications/CMake.app/Contents/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c

# Include any dependencies generated for this target.
include CMakeFiles/dfs.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/dfs.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/dfs.dir/flags.make

CMakeFiles/dfs.dir/crc.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/crc.c.o: crc.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/dfs.dir/crc.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/crc.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/crc.c

CMakeFiles/dfs.dir/crc.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/crc.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/crc.c > CMakeFiles/dfs.dir/crc.c.i

CMakeFiles/dfs.dir/crc.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/crc.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/crc.c -o CMakeFiles/dfs.dir/crc.c.s

CMakeFiles/dfs.dir/dfslib_crypt.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dfslib_crypt.c.o: dfslib_crypt.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object CMakeFiles/dfs.dir/dfslib_crypt.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/dfslib_crypt.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_crypt.c

CMakeFiles/dfs.dir/dfslib_crypt.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/dfslib_crypt.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_crypt.c > CMakeFiles/dfs.dir/dfslib_crypt.c.i

CMakeFiles/dfs.dir/dfslib_crypt.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/dfslib_crypt.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_crypt.c -o CMakeFiles/dfs.dir/dfslib_crypt.c.s

CMakeFiles/dfs.dir/dfslib_random.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dfslib_random.c.o: dfslib_random.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object CMakeFiles/dfs.dir/dfslib_random.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/dfslib_random.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_random.c

CMakeFiles/dfs.dir/dfslib_random.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/dfslib_random.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_random.c > CMakeFiles/dfs.dir/dfslib_random.c.i

CMakeFiles/dfs.dir/dfslib_random.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/dfslib_random.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_random.c -o CMakeFiles/dfs.dir/dfslib_random.c.s

CMakeFiles/dfs.dir/dfslib_string.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dfslib_string.c.o: dfslib_string.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building C object CMakeFiles/dfs.dir/dfslib_string.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/dfslib_string.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_string.c

CMakeFiles/dfs.dir/dfslib_string.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/dfslib_string.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_string.c > CMakeFiles/dfs.dir/dfslib_string.c.i

CMakeFiles/dfs.dir/dfslib_string.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/dfslib_string.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfslib_string.c -o CMakeFiles/dfs.dir/dfslib_string.c.s

CMakeFiles/dfs.dir/dfsrsa.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dfsrsa.c.o: dfsrsa.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building C object CMakeFiles/dfs.dir/dfsrsa.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/dfsrsa.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfsrsa.c

CMakeFiles/dfs.dir/dfsrsa.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/dfsrsa.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfsrsa.c > CMakeFiles/dfs.dir/dfsrsa.c.i

CMakeFiles/dfs.dir/dfsrsa.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/dfsrsa.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfsrsa.c -o CMakeFiles/dfs.dir/dfsrsa.c.s

CMakeFiles/dfs.dir/dnet_crypt.c.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dnet_crypt.c.o: dnet_crypt.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building C object CMakeFiles/dfs.dir/dnet_crypt.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/dfs.dir/dnet_crypt.c.o   -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dnet_crypt.c

CMakeFiles/dfs.dir/dnet_crypt.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/dfs.dir/dnet_crypt.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dnet_crypt.c > CMakeFiles/dfs.dir/dnet_crypt.c.i

CMakeFiles/dfs.dir/dnet_crypt.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/dfs.dir/dnet_crypt.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dnet_crypt.c -o CMakeFiles/dfs.dir/dnet_crypt.c.s

CMakeFiles/dfs.dir/dfs_jni.cc.o: CMakeFiles/dfs.dir/flags.make
CMakeFiles/dfs.dir/dfs_jni.cc.o: dfs_jni.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Building CXX object CMakeFiles/dfs.dir/dfs_jni.cc.o"
	/Library/Developer/CommandLineTools/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/dfs.dir/dfs_jni.cc.o -c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfs_jni.cc

CMakeFiles/dfs.dir/dfs_jni.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/dfs.dir/dfs_jni.cc.i"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfs_jni.cc > CMakeFiles/dfs.dir/dfs_jni.cc.i

CMakeFiles/dfs.dir/dfs_jni.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/dfs.dir/dfs_jni.cc.s"
	/Library/Developer/CommandLineTools/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/dfs_jni.cc -o CMakeFiles/dfs.dir/dfs_jni.cc.s

# Object files for target dfs
dfs_OBJECTS = \
"CMakeFiles/dfs.dir/crc.c.o" \
"CMakeFiles/dfs.dir/dfslib_crypt.c.o" \
"CMakeFiles/dfs.dir/dfslib_random.c.o" \
"CMakeFiles/dfs.dir/dfslib_string.c.o" \
"CMakeFiles/dfs.dir/dfsrsa.c.o" \
"CMakeFiles/dfs.dir/dnet_crypt.c.o" \
"CMakeFiles/dfs.dir/dfs_jni.cc.o"

# External object files for target dfs
dfs_EXTERNAL_OBJECTS =

libdfs.dylib: CMakeFiles/dfs.dir/crc.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dfslib_crypt.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dfslib_random.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dfslib_string.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dfsrsa.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dnet_crypt.c.o
libdfs.dylib: CMakeFiles/dfs.dir/dfs_jni.cc.o
libdfs.dylib: CMakeFiles/dfs.dir/build.make
libdfs.dylib: CMakeFiles/dfs.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles --progress-num=$(CMAKE_PROGRESS_8) "Linking CXX shared library libdfs.dylib"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/dfs.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/dfs.dir/build: libdfs.dylib

.PHONY : CMakeFiles/dfs.dir/build

CMakeFiles/dfs.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/dfs.dir/cmake_clean.cmake
.PHONY : CMakeFiles/dfs.dir/clean

CMakeFiles/dfs.dir/depend:
	cd /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c /Users/punk/Documents/code/JavaProject/holt666/xdagj/src/c/CMakeFiles/dfs.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/dfs.dir/depend
