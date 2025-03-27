cmake_minimum_required(VERSION 3.22.1)

project("bustracker")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set the output directory for the native library
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/../../../../app/libs/${ANDROID_ABI})

# Add C++ library
add_library(serial_port_d SHARED
            SerialPort.cpp)

# Add include directories
target_include_directories(serial_port_d PRIVATE
                         ${CMAKE_CURRENT_SOURCE_DIR})

# Find and link required libraries
find_library(log-lib log)
target_link_libraries(serial_port_d ${log-lib}) 