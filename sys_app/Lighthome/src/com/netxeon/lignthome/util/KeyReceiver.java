package com.netxeon.lignthome.util;

import java.util.List;

import com.netxeon.lignthome.DialogActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class KeyReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "接收到了");
		 
			//Intent intent2 = new Intent("com.netxeon.activitytest");
			
		    Intent intent2 = new Intent(context,DialogActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
	}
	
 

}
