include packages/apps/trigtop/google/google.mk
include packages/apps/trigtop/root_permission/root_permission.mk
include packages/apps/trigtop/sys_copy/sys_copy.mk
PRODUCT_PACKAGES += AdupsFota
PRODUCT_PACKAGES += AdupsFotaReboot
PRODUCT_PACKAGES += Lighthome
PRODUCT_PACKAGES += wizardv10-20170208
PRODUCT_PACKAGES += FileBrowser
PRODUCT_PACKAGES += ProductTest
PRODUCT_PACKAGES += HDMIRxDemo

PRODUCT_PROPERTY_OVERRIDES += persist.wifi.mac.address=02:00:00:00:00:00
#'Auto OSD language' default false
PRODUCT_PROPERTY_OVERRIDES += persist.rtk.cec.setlanguage=false