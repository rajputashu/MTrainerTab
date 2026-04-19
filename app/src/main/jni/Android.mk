LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Here we give our myModule name and source file(s)
LOCAL_MODULE    := CryptoLibrary
LOCAL_SRC_FILES := CryptoLibrary.c
LOCAL_SRC_FILES += base64.cpp
LOCAL_SRC_FILES += EncryptDecrypt.cpp -fexceptions
LOCAL_SRC_FILES += aeskey.c
LOCAL_SRC_FILES += AESHandler.cpp
LOCAL_SRC_FILES += aescrypt.c
LOCAL_SRC_FILES += aestab.c
LOCAL_SRC_FILES += CCyptoLibrary.cpp

include $(BUILD_SHARED_LIBRARY)