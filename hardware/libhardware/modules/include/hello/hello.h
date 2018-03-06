#ifndef ANDROID_HELLO_INTERFACE_H
#define ANDROID_HELLO_INTERFACE_H
#include <hardware/hardware.h>

__BEGIN_DECLS
#define HELLO_HARDWARE_MODULE_ID "hello"//ID
struct hello_module_t {
	struct hw_module_t common;
};//hw_module_t的继承者
struct hello_device_t {
	struct hw_device_t common;
	int fd;
	int (*set_val)(struct hello_device_t* dev, int val);
	int (*get_val)(struct hello_device_t* dev, int* val);
};//hw_device_t的继承者
__END_DECLS

#endif
