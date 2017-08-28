package com.trigtop.trigtop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import com.trigtop.trigtop.data.Data;


public class MainActivity extends Activity {

	private static final String TAG = "trigtop";
	private static final boolean DEBUG = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		Log.e(TAG,"Data.BRAND_RTL=" + Data.BRAND_RTL);
		finish();
	}
}
