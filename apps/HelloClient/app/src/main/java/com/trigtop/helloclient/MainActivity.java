package com.trigtop.helloclient;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int val;
    HelloService hs;
    Button btn_get_set;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hs = new HelloService();

        btn_get_set = (Button) findViewById(R.id.button3);
        tv = (TextView) findViewById(R.id.textView);
        btn_get_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    val = hs.getVal();
                    hs.setVal(val + 1);
                    val = hs.getVal();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                tv.setText("" + val);
            }
        });
    }
}
