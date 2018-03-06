obj-m += hello.o
KERNEL = /home/trigtop/S905X-S912-6.0/out/target/product/q201/obj/KERNEL_OBJ
CROSS_ARCH := ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu-

all:
	make $(CROSS_ARCH) -C $(KERNEL) M=$(PWD) modules

clean:
	rm -rf *.o *.ko
	rm -rf .depend .*.cmd *.mod.c .tmp_versions *.symvers .*.d *.markers *.order
