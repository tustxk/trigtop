package com.trigtop.trigtop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.trigtop.trigtop.data.DBHelper;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

	private static final String TAG = "trigtop";
	private static final boolean DEBUG = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		Log.e("kkk","#######################");
		DBHelper mDB = DBHelper.getInstance(this);
		mDB.insert("user",new String[]{"name","gender","age"},new Object[]{"qiangyu","male",23});
		//mDB.insert("user",new String[]{"name","gender","age"},new Object[]{"aksdjfalf","male",25});
		//mDB.delete("user",new String[]{"age"},new String[]{"25"});
		//mDB.update("user",new String[]{"name","gender","age"},new Object[]{"yangqiangyu","male",24},
		//      new String[]{"name"},new String[]{"qiangyu"});
		List<Map> list = mDB.queryListMap("select * from user",null);
		if(DEBUG) Log.e(TAG,"list="+String.valueOf(list));
		finish();
	}
}
