#include <linux/kobject.h>
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/of.h>
#include <linux/of_gpio.h>
#include <linux/printk.h>
#include <linux/string.h>
#include <linux/gpio.h>
#include <linux/time.h>
#include <linux/workqueue.h>
#include <linux/timer.h>
#include <linux/rtc.h>
#include <linux/slab.h>
#include <linux/delay.h>
#include <linux/proc_fs.h>
#include <linux/sched.h>
#include <asm/uaccess.h>

#define DRIVER_NAME "netxeon_nixie_driver"//驱动名字
//#define OFF 1
#define BOOT_TIME 1

struct netxeon_nixie_gpio {
	const char *name;	
	unsigned int pin;	
	unsigned int active_low;	
	unsigned int state;
};

struct netxeon_dev{
	struct platform_device *pdev;
	struct kobject *kobj;

	struct netxeon_nixie_gpio pwr; //pwr gpio
	struct netxeon_nixie_gpio dio; //data gpio
	struct netxeon_nixie_gpio clk; //clock gpio
	struct netxeon_nixie_gpio stb; //cs pin
	struct timer_list timer;
	struct work_struct work_update;
};

typedef unsigned char uchar;
int firstBoot=1;
static int switch_flag;
static int netxeon_brightness=3;
static int switch_flag_last = 0;
static int sleep_flag = 0;
static struct netxeon_dev *netxeon_nixie_dev = NULL;
void set_brightness(int value);

//对应应用层的write
static ssize_t switch_time_format_att_store(struct device *dev,	struct device_attribute *attr, const char *buf, size_t count) 
{
	sscanf(buf, "%du", &switch_flag);
	return count;
}

static ssize_t netxeon_brightness_att_store(struct device *dev, struct device_attribute *attr, const char *buf, size_t count) 
{
	sscanf(buf, "%du", &netxeon_brightness);
	set_brightness(netxeon_brightness);
	return count;
}

static ssize_t netxeon_sleep_att_store(struct device *dev,
		struct device_attribute *attr,
		const char *buf, size_t count)
{
	sscanf(buf, "%du", &sleep_flag);
	switch_flag_last = !switch_flag;
	return count;
}

//对应应用层的read
static ssize_t switch_time_format_att_show(struct device *dev, struct device_attribute *attr, char *buf)
{
	return sprintf(buf, "%d\n", switch_flag);
}

static ssize_t netxeon_brightness_att_show(struct device *dev, struct device_attribute *attr, char *buf)
{
	return sprintf(buf, "%d\n", netxeon_brightness);
}

static ssize_t netxeon_sleep_att_show(struct device *dev,
		struct device_attribute *attr,
		char *buf)
{
	return sprintf(buf, "%d\n", sleep_flag);
}

static DEVICE_ATTR(switch_time_format,0777,switch_time_format_att_show,switch_time_format_att_store);
static DEVICE_ATTR(netxeon_brightness,0777,netxeon_brightness_att_show,netxeon_brightness_att_store);
static DEVICE_ATTR(netxeon_sleep, 0777, netxeon_sleep_att_show, netxeon_sleep_att_store);

///////////////////////////////////////////////////////////////
#define DELAY_TIME 10
#define POLL_TIME 2000 // polling interval to detect the system time
#define ORIENTATION 1

/*seg and grid are in normal order
  uchar const CODE[][2] = {{0x1f,0x08},{0x06,0x00},{0x1b,0x10},{0x0f,0x10},{0x06,0x18},  //0~4
  {0x0d,0x18},{0x1d,0x18},{0x07,0x00},{0x1f,0x18},{0x0f,0x18},  //5~9
  {0x07,0x00}, // :
  {0x1c,0x18},{0x18,0x18},{0x1e,0x10},{0x19,0x18},{0x17,0x18} // b,t,d,E,A   -- boot  / dead
  };
  */

#if ORIENTATION
//seg and grid are in unusual order1
uchar const CODE[][2] = {{0x17,0x18},{0x06,0x00},{0x0b,0x18},{0x0f,0x10},{0x1e,0x0},  //0~4
	{0x1d,0x10},{0x1d,0x18},{0x07,0x00},{0x1f,0x18},{0x1f,0x10},  //5~9
	{0x02,0x00}, // :
	{0x1c,0x18},{0x18,0x18},{0x0e,0x18},{0x19,0x18},{0x1f,0x08},{0x19,0x08} // b,t,d,E,A,F   -- boot  / dead
};
#else
//seg and grid are in unusual order2
uchar const CODE[][2] = {{0x17,0x18},{0x10,0x08},{0x0b,0x18},{0x19,0x18},{0x1c,0x08},  //0~4
	{0x1d,0x10},{0x1f,0x10},{0x10,0x18},{0x1f,0x18},{0x1d,0x18},  //5~9
	{0x02,0x00}, // :
	{0x1f,0x00},{0x0f,0x00},{0x1b,0x08},{0x0f,0x10},{0x1e,0x18},{0x0e,0x10} // b,t,d,E,A,F   -- boot  / dead
};
#endif

//Send 8bit data to TM1618
void send_8bit(uchar dat)
{
	uchar i;
	for(i=0;i<8;i++)
	{
		gpio_direction_output(netxeon_nixie_dev->clk.pin, 0);

		if(dat&0x01)
			gpio_direction_output(netxeon_nixie_dev->dio.pin, 1);
		else
			gpio_direction_output(netxeon_nixie_dev->dio.pin, 0);

		ndelay(DELAY_TIME);
		gpio_direction_output(netxeon_nixie_dev->clk.pin, 1);

		dat=dat>>1;
	}
	gpio_direction_output(netxeon_nixie_dev->dio.pin, 0);
	gpio_direction_output(netxeon_nixie_dev->clk.pin, 0);

}

//Send command to TM1618
void command(uchar com)
{
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);
	ndelay(DELAY_TIME);
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 0);
	send_8bit(com);
}

void set_brightness(int value)
{
	switch (value) {
		case 0:
			command(0x80);
			break;
		case 1:
			command(0x88);
			break;
		case 2:
			command(0x89);
			break;
		case 3:
			command(0x8A);
			break;
		case 4:
			command(0x8B);
			break;
		case 5:
			command(0x8C);
			break;
		case 6:
			command(0x8D);
			break;
		case 7:
			command(0x8E);
			break;
		case 8:
			command(0x8F);
			break;
		default:
			command(0x8C);
			break;
	}
}
//Dispaly time(Hour+Minute)
int display_time(uchar h,uchar m)
{
	uchar i;
	uchar dat[5]={0,0,0,0,0};
#if ORIENTATION
	dat[0]=m%10;
	dat[1]=m/10;
	dat[2]=10;
	dat[3]=h%10;
	dat[4]=h/10;
#else
	dat[4]=m%10;
	dat[3]=m/10;
	dat[2]=10;
	dat[1]=h%10;
	dat[0]=h/10;
#endif

	if((0==dat[0]) && (0==dat[1]) &&(0==dat[3]) && (0==dat[4]) && (1==firstBoot))
	{
		printk("Booting now...");
		return 0;
	}
	firstBoot=0;

	command(0x01);  //5grid 7seg
	// Auto address mode
	command(0x40);
	command(0xc0);
	for(i=0;i<5;i++){
		send_8bit(CODE[dat[i]][0]);
		send_8bit(CODE[dat[i]][1]);
	}

	set_brightness(netxeon_brightness);
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);
	return 0;

}

//Dispaly 'boot' when power on
void display_boot(void)
{

	command(0x01);  //5grid 7seg
	command(0x40); //auto address write
	command(0xc0);
#if ORIENTATION
	send_8bit(0x18);send_8bit(0x18);
	send_8bit(0xc);send_8bit(0x18);
	send_8bit(0);send_8bit(0);
	send_8bit(0xc);send_8bit(0x18);
	send_8bit(0x1c);send_8bit(0x18);
#else
	send_8bit(CODE[11][0]);send_8bit(CODE[11][1]);
	send_8bit(0x1b);send_8bit(0x03);
	send_8bit(0);send_8bit(0);
	send_8bit(0x1b);send_8bit(0x03);
	send_8bit(CODE[12][0]);send_8bit(CODE[12][1]);
#endif
	set_brightness(netxeon_brightness);
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);

}

//Dispaly 'dead' when system dead
void display_dead(void)
{

	command(0x01);  //5grid 7seg
	command(0x40); // auto address write
	command(0xc0);
	send_8bit(CODE[13][0]);send_8bit(CODE[13][1]);
	send_8bit(CODE[14][0]);send_8bit(CODE[14][1]);
	send_8bit(0);send_8bit(0);
	send_8bit(CODE[15][0]);send_8bit(CODE[15][1]);
	send_8bit(CODE[13][0]);send_8bit(CODE[13][1]);

	set_brightness(netxeon_brightness);
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);

}

//Dispaly 'off' when system dead
void display_off(void)
{

	command(0x01);  //5grid 7seg
	command(0x40); // auto address write
	command(0xc0);
#ifdef OFF

#if ORIENTATION
	send_8bit(0x19);send_8bit(0x8);
	send_8bit(0x19);send_8bit(0x8);
	send_8bit(0);send_8bit(0);
	send_8bit(0x17);send_8bit(0x18);
	send_8bit(0);send_8bit(0);
#else
	send_8bit(CODE[0][0]);send_8bit(CODE[0][1]);
	send_8bit(CODE[16][0]);send_8bit(CODE[16][1]);
	send_8bit(0);send_8bit(0);
	send_8bit(CODE[16][0]);send_8bit(CODE[16][1]);
	send_8bit(0);send_8bit(0);
#endif

#else
	uchar i;
	for(i=0;i<5;i++){
		send_8bit(0);
		send_8bit(0);
	}
#endif
	set_brightness(netxeon_brightness);
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);
}

static uchar stm_min=0,stm_hour=0;
static void netxeon_nixie_work(struct work_struct *work)
{
	struct timeval  t;
	struct rtc_time tm;

	do_gettimeofday(&t);
	t.tv_sec -= sys_tz.tz_minuteswest*60;
	rtc_time_to_tm(t.tv_sec,&tm);
	//printk("UTC time :%d-%d-%d %d:%d:%d /n",tm.tm_year+1900,tm.tm_mon, tm.tm_mday,tm.tm_hour,tm.tm_min,tm.tm_sec);

	if (sleep_flag) {
		display_off();
		return;
	}

	if((stm_min!=tm.tm_min)||(stm_hour!=tm.tm_hour)||(switch_flag != switch_flag_last)){
		stm_min=tm.tm_min;
		stm_hour=tm.tm_hour;
		switch_flag_last = switch_flag;
		if(switch_flag > 0 && tm.tm_hour > 12)
			tm.tm_hour = tm.tm_hour - 12;
		display_time(tm.tm_hour,tm.tm_min);
	}

	//display_time(ldev,tm.tm_min,tm.tm_sec);
}

void nixie_timer_isr(unsigned long data)
{
	schedule_work(&(netxeon_nixie_dev->work_update));
	mod_timer(&netxeon_nixie_dev->timer,jiffies+msecs_to_jiffies(POLL_TIME));
}

static int netxeon_nixie_dt_parse(void)
{	
	int ret;
	printk("##########in %s##########\n",__func__);
	//pwr
	//netxeon_nixie_dev->pwr.pin = 122;
	//ret = gpio_request(netxeon_nixie_dev->pwr.pin, DRIVER_NAME);
	//if (ret < 0 && ret!=-16){
	//	pr_info("ldev->pwr.pin failed\n");
	//	goto gpio_request_failed;
	//}
	//gpio_direction_output(netxeon_nixie_dev->pwr.pin, 1);
	//dio
	netxeon_nixie_dev->dio.pin = 103;
	ret = gpio_request(netxeon_nixie_dev->dio.pin, DRIVER_NAME);
	if (ret < 0 && ret!=-16){
		pr_info("ldev->dio.pin failed\n");
		goto gpio_request_failed;
	}
	gpio_direction_output(netxeon_nixie_dev->dio.pin, 1);
	//clk
	netxeon_nixie_dev->clk.pin = 104;
	ret = gpio_request(netxeon_nixie_dev->clk.pin, DRIVER_NAME);
	if (ret < 0 && ret!=-16){
		pr_info("ldev->clk.pin failed\n");
		goto gpio_request_failed;
	}
	gpio_direction_output(netxeon_nixie_dev->clk.pin, 1);
	//stb
	netxeon_nixie_dev->stb.pin = 18;
	ret = gpio_request(netxeon_nixie_dev->stb.pin, DRIVER_NAME);
	if (ret < 0 && ret!=-16){
		pr_info("ldev->stb.pin failed\n");
		goto gpio_request_failed;
	}
	gpio_direction_output(netxeon_nixie_dev->stb.pin, 1);
	return 0;

gpio_request_failed:
	return ret;
}

static int netxeon_nixie_probe(struct platform_device *ppdev)
{	
	int ret;	
#if BOOT_TIME
	struct rtc_time tm;
	struct timeval  t;
#endif
	printk("##########in %s##########\n",__func__);

	netxeon_nixie_dev->kobj = kobject_create_and_add("netxeon_nixie_obj", NULL); 
	if(netxeon_nixie_dev->kobj == NULL){
		ret = -ENOMEM;
		goto kobj_err;
	}

	ret = sysfs_create_file(&netxeon_nixie_dev->pdev->dev.kobj,&dev_attr_switch_time_format.attr);
	if(ret < 0){
		goto file_err;
	}

	ret = sysfs_create_file(&netxeon_nixie_dev->pdev->dev.kobj,&dev_attr_netxeon_brightness.attr);
	if(ret < 0){
		goto file_err;
	}

	ret = sysfs_create_file(&netxeon_nixie_dev->pdev->dev.kobj, &dev_attr_netxeon_sleep.attr);
	if(ret < 0){
		goto file_err;
	}

	ret = netxeon_nixie_dt_parse();
	if (ret)
		goto parse_err;

	/*Setup timer*/
	setup_timer(&netxeon_nixie_dev->timer, nixie_timer_isr, (unsigned long)netxeon_nixie_dev);
	mod_timer(&netxeon_nixie_dev->timer, jiffies+msecs_to_jiffies(20));

	INIT_WORK(&(netxeon_nixie_dev->work_update), netxeon_nixie_work);

	// display time
#if BOOT_TIME
	do_gettimeofday(&t);
	t.tv_sec -= sys_tz.tz_minuteswest*60;
	rtc_time_to_tm(t.tv_sec,&tm);
	if(switch_flag > 0 && tm.tm_hour > 12)
		tm.tm_hour = tm.tm_hour - 12;
	display_time(tm.tm_hour,tm.tm_min);
#else
	display_boot();
#endif

	pr_info("module probed ok\n");
	return 0;

parse_err:
file_err:
	kobject_del(netxeon_nixie_dev->kobj);  
kobj_err:
	return ret;
}

static int netxeon_nixie_remove(struct platform_device *pdev)
{
	printk("##########in %s##########\n",__func__);

	return 0;
}

static int netxeon_nixie_suspend(struct platform_device *pdev,pm_message_t state)
{
	printk("##########in %s##########\n",__func__);
	//display_off(ldev);
	command(0x80);
	return 0;
}

static int netxeon_nixie_resume(struct platform_device *pdev)
{
	printk("##########in %s##########\n",__func__);
	//display_off(ldev);
	command(0x80);
	return 0;
}

static void netxeon_nixie_shutdown(struct platform_device *pdev)
{
	printk("##########in %s##########\n",__func__);
	//display_off(ldev);
	command(0x80);
}

static struct platform_driver netxeon_nixie_driver = {
	.driver = {
		.owner = THIS_MODULE,
		.name = DRIVER_NAME,
	},
	.probe = netxeon_nixie_probe,
	.remove = __exit_p(netxeon_nixie_remove),
	.suspend = netxeon_nixie_suspend,
	.resume = netxeon_nixie_resume,
	.shutdown = netxeon_nixie_shutdown,
};

static int __init netxeon_nixie_init(void)
{	
	int ret;
	printk("##########in %s##########\n",__func__);

	netxeon_nixie_dev = kzalloc(sizeof(struct netxeon_dev),GFP_KERNEL);
	if(netxeon_nixie_dev == NULL)
	{
		printk("%s get dev memory error\n",__func__);
		return -ENOMEM;
	}

	netxeon_nixie_dev->pdev = platform_device_register_simple(DRIVER_NAME, -1, NULL, 0);  
	if(IS_ERR(netxeon_nixie_dev->pdev))
	{ 
		printk("%s pdev error\n",__func__);
		return -1;
	}

	ret = platform_driver_register(&netxeon_nixie_driver);
	if(ret < 0)
	{
		printk("%s register driver error\n",__func__);
		return ret;
	}

	return 0;
}

static void __exit netxeon_nixie_exit(void)
{
	printk("##########in %s##########\n",__func__);

	cancel_work_sync(&netxeon_nixie_dev->work_update);
	del_timer_sync(&netxeon_nixie_dev->timer);	
	sysfs_remove_file(&netxeon_nixie_dev->pdev->dev.kobj,&dev_attr_switch_time_format.attr);
	sysfs_remove_file(&netxeon_nixie_dev->pdev->dev.kobj,&dev_attr_netxeon_brightness.attr);
	kobject_del(netxeon_nixie_dev->kobj);
	platform_driver_unregister(&netxeon_nixie_driver);
	platform_device_unregister(netxeon_nixie_dev->pdev);
	if(netxeon_nixie_dev != NULL)
		kfree(netxeon_nixie_dev);
	printk("##########%s ok##########\n",__func__);
}

module_init(netxeon_nixie_init);
module_exit(netxeon_nixie_exit);
MODULE_LICENSE("GPL");
MODULE_AUTHOR("netxeon");
