package com.netxeon.lignthome;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netxeon.lignthome.data.DBHelper;
import com.netxeon.lignthome.data.Data;
import com.netxeon.lignthome.data.Shortcut;
import com.netxeon.lignthome.data.ShortcutsAdapter;
import com.netxeon.lignthome.data.ShortcutsAdapter.ShortcutHolder;
import com.netxeon.lignthome.util.Logger;
import com.netxeon.lignthome.util.MemoryCleaner;
import com.netxeon.lignthome.util.Util;
import com.netxeon.lignthome.weather.WeatherUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
	private PackageManager pm;
	private List<Shortcut> mShotcutList;
	private ShortcutsAdapter mAdapter;
	private final String ADDITIONAL = "additional";
	private Activity mActivity;
	private final String mCategory = "favorates";
	private UpdateShortcutsReceiver mUpdateShortcutsReceiver;
	private IntentFilter mIntentFilter;

	// for weather
	private static int mWeatherCode = 3200;
	private static String mWeatherInfoStr;
	private static String mWeatherOtherStr1;
	private static String mWeatherOtherStr2;
	private static String mCityStr;
	private static String editor_location;
	private UpdateWeatherHandler mUpdateWeatherHandler;
	private static final long UPDATE_WEATHER_PER_TIME = 1000 * 60 * 60 * 3;// update weather info per 3 hours
	private static long mLastWeatherUpdate = 0;

	// for memory
	private MemoryCleaner mMemory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mUpdateShortcutsReceiver = new UpdateShortcutsReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Data.ACTION_UPDATE_SHORTCUTS);
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		pm = mActivity.getPackageManager();
		mUpdateWeatherHandler = new UpdateWeatherHandler();
		editor_location = Util.getString(mActivity, WeatherUtils.WEATHER_CITY);
		initWeatherTimer();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_center, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		updateShortcutData();
		getActivity().registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
		updateWeatherViews();
		Util.updateDateDisplay(mActivity);
		if (mMemory == null)
			mMemory = new MemoryCleaner(mActivity);
		MyClickListener clickListener = new MyClickListener();
		mActivity.findViewById(R.id.center_id_date_time_weather_container).setOnClickListener(clickListener);
		mActivity.findViewById(R.id.center_id_memory).setOnClickListener(clickListener);
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mUpdateShortcutsReceiver);
		super.onResume();
	}

	private void updateShortcutData() {
		GridView gridView = (GridView) mActivity.findViewById(R.id.center_id_favorates_container);
		gridView.setOnItemClickListener(new ItemClickListener());
		mShotcutList = DBHelper.getInstance(mActivity).queryByCategory(mCategory);
		Shortcut forAddItem = new Shortcut();
		forAddItem.setComponentName(ADDITIONAL);
		mShotcutList.add(forAddItem);
		mAdapter = new ShortcutsAdapter(mActivity, mShotcutList, pm, true);
		gridView.setAdapter(mAdapter);
		gridView.requestFocus();
	}

	/*
	 * handle the icon click event
	 */
	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			ShortcutHolder holder = (ShortcutHolder) arg1.getTag();
			ComponentName componentName = holder.componentName;
			if (ADDITIONAL.equals(componentName.getPackageName())) {
				Intent editIntent = new Intent(mActivity, AppsActivity.class);
				editIntent.putExtra("category", mCategory);
				startActivity(editIntent);
			} else {
				try {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(componentName);
					startActivity(intent);
				} catch (Exception e) {
					Log.e("error", "MainFragment.ItemClickListener.onItemClick() startActivity failed: " + componentName);
				}
			}

		}
	}

	private class UpdateShortcutsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				updateWeatherData(Util.getString(mActivity, WeatherUtils.WEATHER_CITY), false);
			} else {
				Logger.log(Logger.TAG_PACKAGE, "MainFragment.UpdateShortcutsReceiver.onReceive() update shortcut");
				updateShortcutData();
			}

		}

	}

	private void updateWeatherViews() {
		ImageView weatherIcon = (ImageView) mActivity.findViewById(R.id.center_id_weather_icon);
		TextView weather = (TextView) mActivity.findViewById(R.id.center_id_weather_info);
		TextView weatherOther1 = (TextView) mActivity.findViewById(R.id.center_id_weather_others1);
		TextView weatherOther2 = (TextView) mActivity.findViewById(R.id.center_id_weather_others2);
		TextView city = (TextView) mActivity.findViewById(R.id.center_id_weather_city);
		ProgressBar waiting = (ProgressBar) mActivity.findViewById(R.id.center_id_weather_waiting);
		TextView waitingTips = (TextView) mActivity.findViewById(R.id.center_id_weather_waiting_tips);
		if (mWeatherInfoStr == null && weather != null) {
			waitingTips.setText(R.string.weather_edit_city);
			WeatherUtils.updateWeatherByGPS(mActivity, mUpdateWeatherHandler);
			waiting.setVisibility(View.VISIBLE);
			waitingTips.setVisibility(View.VISIBLE);
		} else if (city != null && weatherIcon != null && weather != null && weatherOther1 != null && weatherOther2 != null) {
			city.setText(mCityStr);
			//weatherIcon.setImageBitmap(mWeatherBitmap);
			if(mWeatherCode >= 0 && mWeatherCode <= 47){
				weatherIcon.setImageResource(Data.getWeatherIcon(mWeatherCode));
			}else{
				weatherIcon.setImageResource(R.drawable.weather3200);
			}
			
			weather.setText(mWeatherInfoStr);
			weatherOther1.setText(mWeatherOtherStr1);
			weatherOther2.setText(mWeatherOtherStr2);
			waiting.setVisibility(View.GONE);
			waitingTips.setVisibility(View.GONE);
		}

	}

	private class UpdateWeatherHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WeatherUtils.MSG_WEATHER_NO_CITY: {
				Util.setString(mActivity.getApplicationContext(), WeatherUtils.WEATHER_CITY, "empty");
				break;
			}
			case WeatherUtils.MSG_WEATHER_OK: {
				WeatherInfo weatherInfo = (WeatherInfo) msg.obj;
				if (weatherInfo != null) {
					/*mWeatherBitmap = weatherInfo.getCurrentConditionIcon();
					mWeatherStr = "Weather:" + weatherInfo.getCurrentText() + "        " + "Temperature:" + weatherInfo.getCurrentTempC() + "ºC " + "( "
							+ weatherInfo.getCurrentTempF() + "ºF )       " + "Wind chill:" + weatherInfo.getWindChill() + "ºF   " + "Wind direction:"
							+ weatherInfo.getWindDirection() + "    " + "Wind speed:" + weatherInfo.getWindSpeed() + "    " + "Humidity:"
							+ weatherInfo.getAtmosphereHumidity() + "        " + "Pressure: " + weatherInfo.getAtmospherePressure() + "   " + "Visibility: "
							+ weatherInfo.getAtmosphereVisibility();*/
					mWeatherInfoStr = weatherInfo.getCurrentText();
					mWeatherOtherStr1 = (weatherInfo.getCurrentTemp()-32)*5/9 + "ºC "+ weatherInfo.getCurrentTemp() +"ºF" ;
					//mWeatherOtherStr1 = weatherInfo.getCurrentTempC() + "ºC " + " (" + weatherInfo.getCurrentTempF() + "ºF )";
					//mWeatherOtherStr1 = "Humidity: "	+ weatherInfo.getAtmosphereHumidity() + "   Pressure: " + weatherInfo.getAtmospherePressure();
					mWeatherOtherStr2 = "Visibility: " + weatherInfo.getAtmosphereVisibility()+ "   Wind speed: " + weatherInfo.getWindSpeed();
					mCityStr = weatherInfo.getLocationCity();
					mWeatherCode = weatherInfo.getCurrentCode();
					Util.setString(mActivity.getApplicationContext(), WeatherUtils.WEATHER_CITY, editor_location);
					updateWeatherViews();
				} else {
					Util.setString(mActivity.getApplicationContext(), WeatherUtils.WEATHER_CITY, "empty");
					Toast.makeText(mActivity.getApplicationContext(), R.string.weather_edit_city_error, Toast.LENGTH_LONG).show();
					Logger.log(Logger.TAG_WEATHER, "MainActivity.UpdateWeatherHandler get weather info error !");
				}
				break;
			}
			}
		}
	};

	private void showEditCityForWeatherDialog() {
		final EditText editor = new EditText(mActivity);
		editor.setSingleLine();
		new AlertDialog.Builder(mActivity).setTitle(R.string.weather_edit_city_dialog_tips).setIcon(android.R.drawable.ic_dialog_info).setView(editor)
				.setPositiveButton(R.string.weather_edit_city_dialog_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String _location = editor.getText().toString();
						if (!TextUtils.isEmpty(_location)) {
							InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(editor.getWindowToken(), 0);
							editor_location = _location;
							updateWeatherData(_location, true);
						} else {
							Toast.makeText(mActivity.getApplicationContext(), R.string.weather_edit_city_dialog_not_empty, Toast.LENGTH_LONG).show();
						}
					}
				}).show();

	}

	private void updateWeatherData(String cityName, boolean forceUpdate) {
		ConnectivityManager connManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		boolean available = false;
		if (info != null)
			available = connManager.getActiveNetworkInfo().isConnected();

		if (available) {
			if (forceUpdate) {
				mLastWeatherUpdate = System.currentTimeMillis();
				WeatherUtils.updateWeatherByCityName(mActivity, mUpdateWeatherHandler, cityName);
				Logger.log(Logger.TAG_WEATHER, "NetworkChangedReceiver.initNetwork() force update weather ");
			} else if (System.currentTimeMillis() - mLastWeatherUpdate > UPDATE_WEATHER_PER_TIME) {
				mLastWeatherUpdate = System.currentTimeMillis();
				WeatherUtils.updateWeatherByCityName(mActivity, mUpdateWeatherHandler, Util.getString(mActivity, WeatherUtils.WEATHER_CITY));
				Logger.log(Logger.TAG_WEATHER, "NetworkChangedReceiver.initNetwork() update weather now ");
			} else {
				Logger.log(Logger.TAG_WEATHER, "NetworkChangedReceiver.initNetwork() no need update weather yet ");
			}

		}
	}

	private void initWeatherTimer() {
		Timer mTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateWeatherData(Util.getString(mActivity, WeatherUtils.WEATHER_CITY), false);
			}
		};
		try {
			mTimer.schedule(task, 100, UPDATE_WEATHER_PER_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class MyClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.center_id_date_time_weather_container){
				showEditCityForWeatherDialog();
			}else if(v.getId() == R.id.center_id_memory){
				mMemory.cleanMemory();
			}
		}
	}

}
