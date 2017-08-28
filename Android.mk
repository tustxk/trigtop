#指定Android.mk文件所在的目录
LOCAL_PATH := $(call my-dir)

##################################################
#编译APK应用程序
#重置除LOCAL_PATH变量外的，所有LOCAL_XXX系列变量
include $(CLEAR_VARS)

#定义模块标签，Build系统根据标签决定哪些模块被安装,user eng tests optional
LOCAL_MODULE_TAGS := optional

#指定APP应用名称
LOCAL_PACKAGE_NAME := Trigtop

#LOCAL_CERTIFICATE := platform

#指定源文件列表
LOCAL_SRC_FILES := $(call all-java-files-under, java)

#除应用(apk)以LOCAL_PACKAGE_NAME指定模块名以外，其余的模块都以LOCAL_MODULE指定模块名
#LOCAL_MODULE := Trigtop 

#定义模块的分类。根据分类，生成的模块文件会安装到目标系统相应的目录下APPS 
#定义模块的分类。根据分类，生成的模块文件会安装到目标系统相应的目录下。
#例如：APPS：安装到/system/app下；
#SHARED_LIBRARIES：安装到/system/lib下；
#EXECUTABLES：安装到/system/bin下；
#ETC：安装到/system/etc下；
#但是如果同时用LOCAL_MOULE_PATH定义了路径，则安装到该路径。
#LOCAL_MODULE_CLASS := APPS


#指定依赖的c/c++静态库列表。
#LOCAL_STATIC_LIBRARIES :=

#指定模块依赖的c/c++共享库列表
#LOCAL_SHARED_LIBRARIES :=

#指定模块依赖的Java共享库
LOCAL_STATIC_JAVA_LIBRARIES := libjcifs_tsb

include $(BUILD_PACKAGE)

##################################################
#加载第三方jar包
#格式：别名:jar文件路径,不含.jar
include $(CLEAR_VARS)
#指定预编译java库列表。用于预编译模块定义中
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	libjcifs_tsb:libs/jcifs-1.3.18.jar

##################################################
#加载第三方so
#格式：别名:so文件路径,不含.so
#指定预编译c/c++动态和静态库列表。用于预编译模块定义中。
#LOCAL_PREBUILT_LIBS :=

include $(BUILD_MULTI_PREBUILT)
##################################################

include $(call all-makefiles-under,$(LOCAL_PATH))
