package com.trigtop.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by k on 2018/3/6.
 */

public class TrigtopService extends Service {

    String TAG = "TigtopService";
    private IBinder mIBinder = new TrigtopBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "--------->onBind: ");
        return mIBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "--------->onCreate: ");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(TAG, "--------->onStart: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "--------->onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "--------->onDestroy: ");
        super.onDestroy();
    }

}
