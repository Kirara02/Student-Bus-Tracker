cmake_minimum_required(VERSION 3.22.1)

project("bustracker")

# Set the output directory for the native library
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/../../../../app/libs/${ANDROID_ABI})

add_library(serial_port_d SHARED
            SerialPort.c)

target_include_directories(serial_port_d PRIVATE
                         ${CMAKE_CURRENT_SOURCE_DIR})

find_library(log-lib log)
target_link_libraries(serial_port_d ${log-lib}) 