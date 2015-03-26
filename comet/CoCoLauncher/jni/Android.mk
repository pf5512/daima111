LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := cut
LOCAL_CFLAGS    := -Werror 
LOCAL_SRC_FILES := jniclib.cpp jtool.cpp utf8togb.cpp
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

