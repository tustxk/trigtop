#!/system/bin/sh
#########################################################################
# File Name: preinstallApks.sh
# Author: ke.xiao
# mail: ke.xiao@netxeon.com
# Created Time: Mon 09 Jan 2017 02:00:10 PM CST
#########################################################################
DisplayID=$(busybox grep "ro.build.display.id=" /system/build.prop|busybox awk -F "=" '{print $NF}' )
DateNum=$(busybox grep "ro.fota.version=${DisplayID}" /system/build.prop|busybox awk -F "_" '{print $NF}' )
APP_MARK=/data/local/symbol_data_${DateNum}
if [ ! -e $APP_MARK ]
then
	DIR=/system/vendor/netxeon/preinstall
	DATA_DIR=/data/app
	cd $DIR
	for apk in $(ls $DIR)
	do
		echo "cp $DIR/$apk $DATA_DIR"
		echo "chown system:system $DATA_DIR/$apk"
		echo "chmod 644 $DATA_DIR/$apk"
		cp $DIR/$apk $DATA_DIR
		chown system:system $DATA_DIR/$apk
		chmod 644 $DATA_DIR/$apk
	done
	touch $APP_MARK
	echo "data app sleep 1"
	sleep 1
fi
