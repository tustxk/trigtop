LOCAL_PATH := $(call my-dir)

#wizardv10-20170208
###########################################################
include $(CLEAR_VARS)
LOCAL_MODULE := wizardv10-20170208
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_CLASS := APPS
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
include $(BUILD_PREBUILT)
###########################################################

#all-makefiles-under,$(LOCAL_PATH)
###########################################################
include $(call all-makefiles-under,$(LOCAL_PATH))
###########################################################
