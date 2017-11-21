package com.netxeon.lignthome.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netxeon.lignthome.util.Logger;


public class PackageUninstalledReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED") && intent.getExtras().getBoolean(Intent.EXTRA_DATA_REMOVED)) {
			String packageName = intent.getDataString();
			Logger.log(Logger.TAG_PACKAGE, "PackageUninstalledReceiver.onReceiver---original str:" + packageName);
			if(packageName == null || packageName.isEmpty()) return;
			packageName = packageName.substring(8);
			Logger.log(Logger.TAG_PACKAGE, "PackageUninstalledReceiver.onReceiver---handled:" + packageName);
			DBHelper.getInstance(context).deleteByPackageName(packageName);
			context.sendBroadcast(new Intent(Data.ACTION_UPDATE_SHORTCUTS));
        }
	}
}
