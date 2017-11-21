obj-m += netxeon_nixie.o
KERNEL = `pwd`/../../../../../../../../../../openwrt/build_dir/target-aarch64_cortex-a53+neon_glibc-2.19/linux-rtd1295_mnas_emmc/linux-4.1.7
CROSS_ARCH := ARCH=arm64 CROSS_COMPILE=`pwd`/../../../../../../../../../../openwrt/staging_dir/toolchain-aarch64_cortex-a53+neon_gcc-4.9-linaro_glibc-2.19/bin/aarch64-openwrt-linux-gnu-

all:
	make $(CROSS_ARCH) -C $(KERNEL) M=$(PWD) modules
	
clean:
	rm -rf *.o *.ko
	rm -rf .depend .*.cmd *.mod.c .tmp_versions *.symvers .*.d *.markers *.order
