/*
 *  * hello.c -- A simple virtual char device driver
 *   */
#include <linux/module.h>  
#include <linux/types.h>  
#include <linux/fs.h>  
#include <linux/errno.h>  
#include <linux/mm.h>  
#include <linux/sched.h>  
#include <linux/init.h>  
#include <linux/cdev.h>  
#include <linux/slab.h>
#include <asm/io.h>  
#include <linux/device.h>
#include <asm/uaccess.h>  


#define DEVICE_SUM 1

static int hello_open(struct inode *inode, struct file *filp);
static int hello_release(struct inode *, struct file *filp);
static ssize_t hello_read(struct file*, char*, size_t, loff_t*);
static ssize_t hello_write(struct file*, const char*, size_t, loff_t*);

/* the major device number */
static int s_hello_major = 0;
static int s_hello_minor = 0;


static struct class* g_s_hello_class = NULL;


/* init the file_operations structure */
struct file_operations g_hello_fops =
{
	.owner = THIS_MODULE,
	.open = hello_open,
	.release = hello_release,
	.read = hello_read,
	.write = hello_write,
};

/* define a cdev device */
struct cdev *g_cdev;

static int g_s_var = 0; /* global var */


/* open device */
static int hello_open(struct inode *inode, struct file *filp)
{
	int ret = 0;
	printk("KERNEL:open success.\n");
	return ret;
}

/* release device */
static int hello_release(struct inode *inode, struct file *filp)
{
	printk("KERNEL:release success.\n");
	return 0;
}

/* read device */
static ssize_t hello_read(struct file *filp, char *buf, size_t len, loff_t *off)
{
	printk("KERNEL:reading...\n");
	if(copy_to_user(buf, &g_s_var, sizeof(int)))
	{
		return -EFAULT;
	}
	return sizeof(int);
}

/* write device */
static ssize_t hello_write(struct file *filp, const char *buf, size_t len, loff_t *off)
{
	printk("KERNEL:writing...\n");
	if(copy_from_user(&g_s_var, buf, sizeof(int)))
	{
		return -EFAULT;
	}
	return sizeof(int);
}

/* module init */
static int __init hello_init(void)
{
	int ret = 0;
	struct device* temp = NULL;
	dev_t devno;

	/*①主设备编号和次设备编号的申请*/
	s_hello_major = 0;//主设备编号
	s_hello_minor = 0;//次设备编号
	devno = 0;//devno = MKDEV(int major, int minor);

	/*动态分配主设备和从设备号*/
	ret = alloc_chrdev_region(&devno, s_hello_minor, DEVICE_SUM, "hello");
	if(ret < 0) {
		printk(KERN_ALERT"Failed to alloc char dev region.\n");
		goto fail;
	}
	s_hello_major = MAJOR(devno);
	s_hello_minor = MINOR(devno);

	/*②创建并初始化cdev加入到链表中*/
	g_cdev = cdev_alloc();
	g_cdev->owner = THIS_MODULE;
	g_cdev->ops = &g_hello_fops;
	if ((ret = cdev_add(g_cdev, devno, 1)))
	{
		printk(KERN_NOTICE "Error %d adding hello.\n", ret);
		return 0;
	}
	else
		printk("hello register success.\n");

	/*③在sysfs中创建一些必要的数据结构*/
	/*在/sys/class/目录下创建设备类别目录hello*/
	g_s_hello_class = class_create(THIS_MODULE, "hello");
	if(IS_ERR(g_s_hello_class)) {
		ret = PTR_ERR(g_s_hello_class);
		printk(KERN_ALERT"Failed to create hello class.\n");
		goto destroy_cdev;
	}
	/*在/dev/目录和/sys/class/hello目录下分别创建设备文件hello*/
	temp = device_create(g_s_hello_class, NULL, devno, "%s", "hello");
	if(IS_ERR(temp)) {
		ret = PTR_ERR(temp);
		printk(KERN_ALERT"Failed to create hello device.");
		goto destroy_class;
	}
	printk("hello_init ok!\n");
	return ret;
destroy_class:
	class_destroy(g_s_hello_class);
destroy_cdev:
	cdev_del(g_cdev);
fail:
	return ret;
}

/* module exit */
static void __exit hello_exit(void)
{
	dev_t devno = MKDEV(s_hello_major, 0);

	device_destroy(g_s_hello_class, devno);

	class_destroy(g_s_hello_class);

	/* remove cdev from kernel */
	cdev_del(g_cdev);

	/* free the dev structure */
	if(g_cdev)
		kfree(g_cdev);
	g_cdev = NULL;

	/* unregister the device driver */
	unregister_chrdev_region(devno, 1);

	printk("hello_exit ok!\n");
}

/* module register */
module_init(hello_init);
module_exit(hello_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("tustxk");
