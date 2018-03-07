package com.trigtop.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.trigtop.server.ITrigtopAidlInterface;

public class MainActivity extends AppCompatActivity {

    private TrigtopConnection mConnection = new TrigtopConnection();
    private ITrigtopAidlInterface mBinder;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent service = new Intent();
        service.setComponent(new ComponentName("com.trigtop.server",
                "com.trigtop.server.TrigtopService"));
        bindService(service, mConnection, BIND_AUTO_CREATE);

        tv = (TextView) findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mBinder.setVal(mBinder.getVal() + 1);
                            tv.setText("" + mBinder.getVal());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private class TrigtopConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 创建一个 AIDL 接口对象
            mBinder = ITrigtopAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
