package com.netxeon.lignthome;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.admin.DevicePolicyManager;
import android.app.WallpaperManager;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.netxeon.lignthome.data.Data;
import com.netxeon.lignthome.data.NetworkChangedReceiver;
import com.netxeon.lignthome.util.Logger;
import com.netxeon.lignthome.util.ReadSignutures;
import com.netxeon.lignthome.util.Util;
import java.util.Timer;
import java.util.TimerTask;

//import android.app.admin.DevicePolicyManager;
public class ActivityMain extends Activity {
    private static String TAG = "lighterhome";
    WallpaperManager wallpaperManager;
    Resources res;
    Bitmap bitmap;
	private Set<Integer> center_icon_ids;
	private PackageManager mPM;
	private NetworkChangedReceiver mDateChangedReceiver;
	private IntentFilter mFilter;
	private View mRootView;
	private String mPlatform;
    private Resources mRes;
    private boolean isIntent = false;
    private boolean isIntentPause = false;
    private  boolean isFirst=true;
    private boolean shortPress = false;
	private String mFTimerCount = "";
	private TimerTask mFtimerTaskText;
	private Timer mFtimerText;

    //DevicePolicyManager mDPM;
    //ComponentName mDeviceAdminSample;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRes = getResources();
//                if (!Util.isValidSystem()) {
//                Log.e("error", mRes.getString(R.string.invalid_system));
//                finish();
//                }
		 
		 
	
		 // 静态方法，判断是否存在当前证书下的包
//		 ReadSignutures.checkIt(this);

		mPlatform = Util.getPlatform();
		// 获取prefile的SharedPreferences的存储内容（存的是以前的数据库）
		int lastDatabaseVersion = Util.getInt(ActivityMain.this,
				Data.PRE_DB_VERSION);
		// 获取到数据库版本
		final int newDatabaseVersion = Util.getNewDatabaseVersion(
				ActivityMain.this, Data.PRE_DB_VERSION);
	     //获取设备管理接收者
		/*mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName("com.netxeon.lignthome","com.netxeon.lignthome.MyAdmin");
		mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		 boolean isAdminActive = mDPM.isAdminActive(mDeviceAdminSample);
		if(!isAdminActive){
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
			//mDPM.setActiveAdmin(mDeviceAdminSample,true);
			  // startActivityForResult(intent, 1);
			   startActivity(intent);
		}*/
	
		if (lastDatabaseVersion < newDatabaseVersion) {
			new Thread() {
				public void run() {
					// 在线程里更新数据库
					Util.copyDatabaseFromAssert(ActivityMain.this,
							newDatabaseVersion);
				}
			}.start();

		}
		// 设置成无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置窗体全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        // 设置布局
        setContentView(R.layout.activity_main);

        // 若状态为空，设置fragment初始
		//if (savedInstanceState == null) {
			Util.translateFragmentToDefault(this);
		//}
		initViews();
		initNetworkDisplay();
		// 把Data类下的一些id加入集合
		center_icon_ids = Data.getCenterIconIds();
		mPM = getPackageManager();
		this.mFtimerText = new Timer();
		this.mFtimerTaskText = null;
		this.mFTimerCount = "";

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// press home key to display the center fragment
               isIntentPause = true;
		Util.translateFragmentToDefault1(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播

		if (isIntent){
			isIntent =false;
			startActivity(new Intent(this,ActivityMain.class));
		}
		registerReceiver(mDateChangedReceiver, mFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
        isIntentPause = true;
		// 注销广播
		unregisterReceiver(mDateChangedReceiver);
	}

	// 长按

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Log.i("wh", "onKeyLongPress");
		if (keyCode == 0) {
			shortPress = false;

//			Toast.makeText(this, "longPress_KEYCODE_0", Toast.LENGTH_LONG)
//					.show();

			return true;
		}
		// Just return false because the super call does always the same
		// (returning false)
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        isIntentPause = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键,按下返回键还是显示默认的fragment
			startActivity(new Intent(this,ActivityMain.class));
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			showBackgroundSelector();
			return true;
		} else if (event.getScanCode() == 392) {// for all apps
			startActivity(new Intent(this, AppsActivity.class));
		}
	
		 //自己
		else if (keyCode == 0) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				Log.i("bo", "KEYCODE_0");
				event.startTracking();
				if (event.getRepeatCount() == 0) {
				shortPress = true;
				}
				return true;
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_8) {
			mFTimerCount += "trigtop";
		}
		if (this.mFTimerCount.contains("trigtop") && this.mFtimerTaskText == null) {
			this.mFtimerTaskText = new TimerTask() {
				public void run() {
					if (mFTimerCount.equals("trigtoptrigtoptrigtop")) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						ComponentName componentName = new ComponentName("com.autoreboot.android", "com.autoreboot.android.MainActivity");
						intent.setComponent(componentName);
						startActivity(intent);
					}
					mFTimerCount = "";
					mFtimerTaskText = null;
				}
			};
			this.mFtimerText.schedule(this.mFtimerTaskText, 2000);
		}

		return super.onKeyDown(keyCode, event);
	}

	private void initNetworkDisplay() {
		mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_TIME_CHANGED);
		mFilter.addAction(Intent.ACTION_DATE_CHANGED);
		mFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		// **广播给接收器
		mDateChangedReceiver = new NetworkChangedReceiver(this);
	}

	// 初始view，fragmentswitchlistener其实就是（焦点改变事件）OnFocusChangeListener
	private void initViews() {
		mRootView = findViewById(R.id.main_id_root);
//        mRootView.setBackgroundResource(Util.getInt(ActivityMain.this,
//                Data.PRE_BACKGROUND, R.drawable.bg_white));
		FragmentSwitchListener swtchListener = new FragmentSwitchListener();
		ImageView switchLeft = (ImageView) findViewById(R.id.main_id_left);
		ImageView switchRight = (ImageView) findViewById(R.id.main_id_right);
		// 监听
		switchLeft.setOnFocusChangeListener(swtchListener);
		switchRight.setOnFocusChangeListener(swtchListener);
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        res = getResources();
        SharedPreferences setting = getSharedPreferences("com.example.hr_jie", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {// 第一次则跳转到欢迎页面
            setting.edit().putBoolean("FIRST", false).commit();
            bitmap = BitmapFactory.decodeResource(res, Util.getInt(ActivityMain.this,
                    Data.PRE_BACKGROUND, R.drawable.bg_white));
            try {
                wallpaperManager.setBitmap(bitmap);
                isFirst = false;
                Util.setBoolean(this, "isFirst", isFirst);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

	public void onIconClick(View view) {
		int id = view.getId();

		if (id == R.id.main_id_left || id == R.id.main_id_right) {
			Util.translateFragment(ActivityMain.this, id);

		} else if (id == R.id.center_id_apps) {
			startActivity(new Intent(ActivityMain.this, AppsActivity.class));
			Logger.log(Logger.TAG_PACKAGE,
					"ActivityMain onIconClick() start apps view");

		} else if (center_icon_ids.contains(id)) {
			// 集合中是否含有这个id
			if (id == R.id.center_id_setting) {
				isIntent =true;
				Intent intent = new Intent();
				// Android 4.4
				// intent.setComponent(new ComponentName("com.mbx.settingsmbox",
				// "com.mbx.settingsmbox.SettingsMboxActivity"));
				// Android5.1
				intent.setComponent(new ComponentName(
							"com.android.settings",
							"com.android.settings.Settings"));

				// 通过使用componentName跳转启动第三方应用。
				try {
					startActivity(intent);
				} catch (Exception e) {
					// Log.e("error",
					// "MainActivity.onIconClick() startActivity failed: "
					// + String.valueOf(view.getTag()));
					e.printStackTrace();
				}

			} else {
				String packageName = String.valueOf(view.getTag());

				// 文件管理
			if (id == R.id.center_id_file) {// different platform need to start
											// different file manager
				if (mPlatform.equals(Data.BRAND_RK)) {
					packageName = mRes.getString(R.string.package_file_rockchip);
				} else if (mPlatform.equals(Data.BRAND_AML)) {
					packageName = mRes.getString(R.string.package_file_amlogic);
				} else if (mPlatform.equals(Data.BRAND_AW)) {
					packageName = mRes.getString(R.string.package_file_aw);
				} else if (mPlatform.equals(Data.BRAND_RTL)) {
					packageName = mRes.getString(R.string.package_file_realtek);
				}
					// packageName =
					// mRes.getString(R.string.package_file_amlogic);

				} else if (id == R.id.center_id_xbmc) {
					Log.i("test", "center_id_xbmc");
					Log.i("test", mRes.getString(R.string.package_kodi));
					/*if (isPackageInstalled(mRes
					  .getString(R.string.package_kodi))) {
					  Log.i("test", "kodi");
					  packageName = mRes.getString(R.string.package_kodi);
					  } else {
					  Log.i("test", "package_xbmc");
					  packageName = mRes.getString(R.string.package_xbmc);
					  }*/
					packageName = mRes.getString(R.string.package_kodi);
				} 
				//				else if (id == R.id.center_id_memorys) {
				//					Log.i("test", "center_id_memory");
				//					if (isPackageInstalled(mRes
				//							.getString(R.string.package_clean))) {
				//						packageName = mRes.getString(R.string.package_clean);
				//					}
				//				}

				try {
					startActivity(mPM.getLaunchIntentForPackage(packageName));
				} catch (Exception e) {
					Log.e("error",
							"MainActivity.onIconClick() startActivity failed: "
									+ String.valueOf(view.getTag()));
				}
			}

		}
	}

	private boolean isPackageInstalled(String packageName) {
		// 获取所有已安装程序的包信息
		List<PackageInfo> pInfoList = mPM.getInstalledPackages(0);
		for (PackageInfo info : pInfoList) {

			if (info.packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	// 携带数据跳转
	private void showBackgroundSelector() {
		startActivityForResult(new Intent(this,
				BackgroundSelectorActivity.class), 0);
	}

	// 从backgroundActivity返回的数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
                Bundle bundle = data.getExtras(); // data为B中回传的Intent
                int bgId = bundle.getInt(Data.PRE_BACKGROUND);
                 bitmap= BitmapFactory.decodeResource(res,bgId);
                try
                {
                    wallpaperManager.setBitmap(bitmap);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
               // Util.setInt(ActivityMain.this, Data.PRE_BACKGROUND, bgId);
                break;
            default:
			break;
		}
	}
    // 重写焦点改变监听事件
    private class FragmentSwitchListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !isIntentPause) {
                Util.translateFragment(ActivityMain.this, v.getId());
            }
            isIntentPause = false;
        }

    }
}
