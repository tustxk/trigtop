#define LOG_TAG "Hello-hal"
#include <hardware/hardware.h>
#include <hello/hello.h>

#include <sys/mman.h>

#include <dlfcn.h>

#include <cutils/ashmem.h>
#include <cutils/log.h>

#include <fcntl.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <string.h>
#include <stdlib.h>

#include <cutils/log.h>
#include <cutils/atomic.h>

#define MODULE_NAME "Hello"
char const * const device_name = "/dev/hello" ;
static int hello_device_open(const struct hw_module_t* module, const char* name, struct hw_device_t** device);
static int hello_device_close(struct hw_device_t* device);
static int hello_set_val(struct hello_device_t* dev, int val);
static int hello_get_val(struct hello_device_t* dev, int* val);

static struct hw_module_methods_t hello_module_methods = {
	.open = hello_device_open,
};
static int hello_device_open(const struct hw_module_t* module, const char* name, struct hw_device_t** device)
{
	struct hello_device_t* dev;
	char name_[64];
	//pthread_mutex_t lock;
	dev = (struct hello_device_t*)malloc(sizeof(struct hello_device_t));
	if(!dev) {
		ALOGE("failed to alloc space");
		return -EFAULT;
	}
	ALOGE("hello_device_open");
	memset(dev, 0, sizeof(struct hello_device_t));

	dev->common.tag = HARDWARE_DEVICE_TAG;
	dev->common.version = 0;
	dev->common.module = (hw_module_t*)module;
	dev->common.close = hello_device_close;
	dev->set_val = hello_set_val;
	dev->get_val = hello_get_val;

	//pthread_mutex_lock(&lock);
	dev->fd = -1 ;
	snprintf(name_, 64, device_name, 0);
	dev->fd = open(name_, O_RDWR);
	if(dev->fd == -1) {
		ALOGE("failed to open %s !-- %s.", name_,strerror(errno));
		free(dev);
		return -EFAULT;
	}
	//pthread_mutex_unlock(&lock);
	*device = &(dev->common);
	ALOGI("open HAL hello successfully.");
	return 0;
}

static int hello_device_close(struct hw_device_t* device) {
	struct hello_device_t* hello_device = (struct hello_device_t*)device;
	if(hello_device) {
		close(hello_device->fd);
		free(hello_device);
	}
	return 0;
}
static int hello_set_val(struct hello_device_t* dev, int val) {
	ALOGI("set value to device.");
	write(dev->fd, &val, sizeof(val));
	return 0;
}
static int hello_get_val(struct hello_device_t* dev, int* val) {
	if(!val) {
		ALOGE("error val pointer");
		return -EFAULT;
	}
	read(dev->fd, val, sizeof(*val));
	ALOGI("get value  from device");
	return 0;
}

struct hello_module_t HAL_MODULE_INFO_SYM = {
	.common = {
		.tag                = HARDWARE_MODULE_TAG,
		//.module_api_version = FINGERPRINT_MODULE_API_VERSION_2_0,
		.hal_api_version    = HARDWARE_HAL_API_VERSION,
		.id                 = HELLO_HARDWARE_MODULE_ID,
		.name               = "Demo tustxk hello HAL",
		.author             = "The Android Open Source Project",
		.methods            = &hello_module_methods,
	},
};
