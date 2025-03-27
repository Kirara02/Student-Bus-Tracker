/*
 * Copyright 2009-2011 Cedric Priscal
 * Updated to modern C++ by Claude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string>
#include <memory>
#include <stdexcept>
#include <jni.h>
#include <android/log.h>

// Modern C++ logging macros
#define LOG_TAG "SerialPort"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Modern C++ SerialPort class
class SerialPort {
private:
    static constexpr const char* DEFAULT_PORT = "/dev/ttyACM0";
    static constexpr int DEFAULT_BAUDRATE = 9600;
    
    static speed_t getBaudrate(int baudrate) {
        static const std::pair<int, speed_t> baudrates[] = {
            {0, B0}, {50, B50}, {75, B75}, {110, B110},
            {134, B134}, {150, B150}, {200, B200}, {300, B300},
            {600, B600}, {1200, B1200}, {1800, B1800}, {2400, B2400},
            {4800, B4800}, {9600, B9600}, {19200, B19200},
            {38400, B38400}, {57600, B57600}, {115200, B115200},
            {230400, B230400}, {460800, B460800}, {500000, B500000},
            {576000, B576000}, {921600, B921600}, {1000000, B1000000},
            {1152000, B1152000}, {1500000, B1500000}, {2000000, B2000000},
            {2500000, B2500000}, {3000000, B3000000}, {3500000, B3500000},
            {4000000, B4000000}
        };
        
        for (const auto& [rate, speed] : baudrates) {
            if (rate == baudrate) return speed;
        }
        throw std::invalid_argument("Invalid baudrate");
    }

public:
    static jobject openPort(JNIEnv* env, jclass thiz) {
        try {
            // Set permissions
            system("hdxsu -c \"chmod 777 /dev/ttyACM0\"");
            
            // Open port
            LOGD("Opening serial port %s", DEFAULT_PORT);
            int fd = ::open(DEFAULT_PORT, O_RDWR);
            if (fd == -1) {
                LOGE("Failed to open port: %s", strerror(errno));
                return nullptr;
            }
            LOGD("open() fd = %d", fd);

            // Configure port
            struct termios cfg;
            if (tcgetattr(fd, &cfg) == -1) {
                LOGE("Failed to get port attributes: %s", strerror(errno));
                ::close(fd);
                return nullptr;
            }

            cfmakeraw(&cfg);
            speed_t speed = getBaudrate(DEFAULT_BAUDRATE);
            cfsetispeed(&cfg, speed);
            cfsetospeed(&cfg, speed);

            // Set additional port parameters
            cfg.c_cflag &= ~PARENB;  // No parity
            cfg.c_cflag &= ~CSTOPB;  // 1 stop bit
            cfg.c_cflag &= ~CSIZE;
            cfg.c_cflag |= CS8;      // 8 data bits
            cfg.c_cflag |= CREAD;    // Enable receiver
            cfg.c_cflag |= CLOCAL;   // Ignore modem control lines
            cfg.c_cflag |= HUPCL;    // Hang up on last close
            cfg.c_lflag &= ~ICANON;  // Raw input
            cfg.c_lflag &= ~ECHO;    // Disable echo
            cfg.c_lflag &= ~ECHOE;   // Disable erasure
            cfg.c_lflag &= ~ECHONL;  // Disable new-line echo
            cfg.c_lflag &= ~ISIG;    // Disable interpretation of INTR, QUIT and SUSP
            cfg.c_iflag &= ~IXON;    // Disable XON/XOFF flow control
            cfg.c_iflag &= ~IXOFF;   // Disable XON/XOFF flow control
            cfg.c_iflag &= ~IXANY;   // Disable any character to restart output
            cfg.c_iflag &= ~IGNBRK;  // Don't ignore BREAK condition
            cfg.c_iflag &= ~BRKINT;  // Don't treat BREAK as SIGINT
            cfg.c_iflag &= ~PARMRK;  // Don't mark parity errors
            cfg.c_iflag &= ~INLCR;   // Don't translate NL to CR
            cfg.c_iflag &= ~IGNCR;   // Don't ignore CR
            cfg.c_iflag &= ~ICRNL;   // Don't translate CR to NL
            cfg.c_oflag &= ~OPOST;   // Raw output
            cfg.c_oflag &= ~ONLCR;   // Don't translate CR to NL

            if (tcsetattr(fd, TCSANOW, &cfg) == -1) {
                LOGE("Failed to set port attributes: %s", strerror(errno));
                ::close(fd);
                return nullptr;
            }

            // Create FileDescriptor object
            jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
            jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor, "<init>", "()V");
            jfieldID descriptorID = env->GetFieldID(cFileDescriptor, "descriptor", "I");
            
            jobject mFileDescriptor = env->NewObject(cFileDescriptor, iFileDescriptor);
            env->SetIntField(mFileDescriptor, descriptorID, fd);

            return mFileDescriptor;
        } catch (const std::exception& e) {
            LOGE("Error opening port: %s", e.what());
            return nullptr;
        }
    }

    static void closePort(JNIEnv* env, jobject thiz) {
        try {
            jclass SerialPortClass = env->GetObjectClass(thiz);
            jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");

            jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
            jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

            jobject mFd = env->GetObjectField(thiz, mFdID);
            if (mFd != nullptr) {
                jint descriptor = env->GetIntField(mFd, descriptorID);
                if (descriptor != -1) {
                    LOGD("close(fd = %d)", descriptor);
                    ::close(descriptor);
                }
            }
        } catch (const std::exception& e) {
            LOGE("Error closing port: %s", e.what());
        }
    }
};

// JNI registration
static JNINativeMethod gMethods[] = {
    {"getdesc", "()Ljava/io/FileDescriptor;", (void*)SerialPort::openPort},
    {"close", "()V", (void*)SerialPort::closePort},
};

static const char* kClassName = "gidb/com/Sderb";

static bool registerNativeMethods(JNIEnv* env, const char* className,
                                JNINativeMethod* methods, int numMethods) {
    jclass clazz = env->FindClass(className);
    if (!clazz) {
        LOGE("Failed to find class %s", className);
        return false;
    }
    
    if (env->RegisterNatives(clazz, methods, numMethods) < 0) {
        LOGE("Failed to register native methods for %s", className);
        return false;
    }
    
    return true;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_4) != JNI_OK) {
        LOGE("Failed to get JNIEnv");
        return -1;
    }

    if (!registerNativeMethods(env, kClassName, gMethods,
                             sizeof(gMethods) / sizeof(gMethods[0]))) {
        LOGE("Failed to register native methods");
        return -1;
    }

    return JNI_VERSION_1_4;
} 