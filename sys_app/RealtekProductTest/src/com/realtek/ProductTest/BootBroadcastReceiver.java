package com.realtek.ProductTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.os.SystemProperties;
import android.provider.Settings;

public class BootBroadcastReceiver extends BroadcastReceiver {
    String TAG="ProductTest.BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "msg: "+intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            int isTest = Settings.Global.getInt(context.getContentResolver(), "product_test", 0);
            Log.d(TAG, "isTest: "+isTest);
            if (isTest != 1) {
                Log.d(TAG, "start ProductTest");
                Intent intent1 = new Intent(context , MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                //Toast.makeText(context, "Launch ProductTest .."+MainActivity.class, Toast.LENGTH_LONG).show();
                //執行一個Activity

                //Intent intent2 = new Intent(context , MyService.class);
                //context.startService(intent2);
                //執行一個Service
            }
        }
    }
}
