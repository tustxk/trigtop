#define LOG_TAG "HelloService-jni"
#include <jni.h>
#include <JNIHelp.h>
#include <android_runtime/AndroidRuntime.h>
#include <utils/misc.h>
#include <utils/Log.h>
#include <hardware/hardware.h>
#include <hello/hello.h>
#include <stdio.h>
namespace android
{
	struct hello_device_t* hello_device = NULL;
	static void hello_setVal(JNIEnv* env, jobject clazz, jint value) {
		int val = value;
		ALOGI("set value %d to device.", val);
		if(!hello_device) {
			ALOGI("device is not open.");
			return;
		}
		hello_device->set_val(hello_device, val);
	}
	static jint hello_getVal(JNIEnv* env, jobject clazz) {
		int val = 0;
		if(!hello_device) {
			ALOGI("device is not open.");
			return val;
		}
		hello_device->get_val(hello_device, &val);

		ALOGI("get value %d from device.", val);

		return val;
	}
	static inline int hello_device_open(const hw_module_t* module, struct hello_device_t** device) {
		return module->methods->open(module, HELLO_HARDWARE_MODULE_ID, (struct hw_device_t**)device);

	}
	static jboolean hello_init(JNIEnv* env, jclass clazz) {
		hello_module_t* module;

		ALOGI("initializing......");
		if(hw_get_module(HELLO_HARDWARE_MODULE_ID, (const struct hw_module_t**)&module) == 0) {
			ALOGI("hello Stub found.");
			if(hello_device_open(&(module->common), &hello_device) == 0) {
				ALOGI("hello device is open.");
				return 0;
			}
			ALOGE("failed to open hello device.");
			return -1;
		}
		ALOGE("failed to get hello stub module.");
		return -1;
	}
	static const JNINativeMethod method_table[] = {
		{"init_native", "()Z", (void*)hello_init},
		{"setVal_native", "(I)V", (void*)hello_setVal},
		{"getVal_native", "()I", (void*)hello_getVal},
	};
	int register_android_server_HelloService(JNIEnv *env) {
		return jniRegisterNativeMethods(env, "com/trigtop/server/TrigtopBinder", method_table, NELEM(method_table));
	}
};

using namespace android;

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("GetEnv failed!");
        return result;
    }
    ALOG_ASSERT(env, "Could not retrieve the env!");

    register_android_server_HelloService(env);

    return JNI_VERSION_1_4;
}

