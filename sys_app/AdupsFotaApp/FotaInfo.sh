#!/bin/bash

echo ""
echo "# begin fota properties"
echo "ro.fota.platform=RTK1296_6.0"
#type info: phone, pad ,box, tv
echo "ro.fota.type=box"
echo "ro.fota.app=5"
echo "ro.fota.id=mac"
#oem info
echo "ro.fota.oem=zwx1296_6.0"
#model info, Settings->About phone->Model number
FotaDevice=$(grep "ro.product.model=" "$1"|awk -F "=" '{print $NF}' )
echo "ro.fota.device=$FotaDevice" | sed 's/\$//' | sed 's/\///' | sed 's/\\//' | sed 's/\&//'
#version number, Settings->About phone->Build number
#FotaVersion=$(grep "ro.build.display.id=" "$1"|awk -F "=" '{print $NF}' )
FotaVersion=$(grep "ro.build.display.id=" "$1"|awk -F "=" '{print $NF}' )`date +_%Y%m%d-%H%M`
echo "ro.fota.version=$FotaVersion" | sed 's/\$//' | sed 's/\///' | sed 's/\\//'
echo "# end fota properties"
