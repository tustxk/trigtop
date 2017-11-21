package com.netxeon.lignthome.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.netxeon.lignthome.CategoryFragment;
import com.netxeon.lignthome.MainFragment;
import com.netxeon.lignthome.R;
import com.netxeon.lignthome.data.DBHelper;
import com.netxeon.lignthome.data.Data;
import com.netxeon.lignthome.data.Shortcut;
//工具类

public class Util {
	private static int mCurrentFragmentIndex = 0;
	private static boolean mSynchronized;

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		if (connManager.getActiveNetworkInfo() != null) {
			//返回当前的网络连接是否可用  
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		//没联网直接返回
		return false;
	}

	
	public static void translateFragmentToDefault(Activity activity) {
		View categoryImg = activity.findViewById(R.id.main_id_category_img);
		View markImg = activity.findViewById(R.id.main_id_mark);
		TextView categoryTips = (TextView) activity.findViewById(R.id.main_id_category_tips);
		//小房子图标
		categoryImg.setBackgroundResource(R.drawable.category_home);
		//底部导航
		markImg.setBackgroundResource(R.drawable.mark_bg_center);
		//R.string.category_home_tips=home
		categoryTips.setText(R.string.category_home_tips);
		Fragment fragment = new MainFragment();
		mCurrentFragmentIndex = 1;
		//FragmentTransaction管理
		FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
		//设置fragment并commit
		transaction.replace(R.id.container, fragment).commitAllowingStateLoss();
		
	}

	//循环改变fragment
	public static void translateFragment(Activity activity, int switchId) {
		Fragment fragment = null;
		int animId = 0;
		if (switchId == R.id.main_id_left) {
			animId = R.anim.main_left_in;
			switch (mCurrentFragmentIndex) {
			case 0: {//最左边往左
				fragment = new CategoryFragment();
				//带个数据
				Bundle bundle = new Bundle();
				bundle.putString("category", "games");
				fragment.setArguments(bundle);
				mCurrentFragmentIndex = 2;
				break;

			}
			case 1: {//中间往左
				fragment = new CategoryFragment();
				Bundle bundle = new Bundle();
				bundle.putString("category", "videos");
				fragment.setArguments(bundle);
				mCurrentFragmentIndex = 0;
				break;
			}
			case 2: {//按home跑这里
				fragment = new MainFragment();
				mCurrentFragmentIndex = 1;
				break;
			}
			}
		} else {
			animId = R.anim.main_right_in;
			switch (mCurrentFragmentIndex) {
			case 0: {
				fragment = new MainFragment();
				mCurrentFragmentIndex = 1;
				break;
			}
			case 1: {//一开始往右
				fragment = new CategoryFragment();
				Bundle bundle = new Bundle();
				bundle.putString("category", "games");
				fragment.setArguments(bundle);
				mCurrentFragmentIndex = 2;
				break;
			}
			case 2: {
				fragment = new CategoryFragment();
				Bundle bundle = new Bundle();
				bundle.putString("category", "videos");
				fragment.setArguments(bundle);
				mCurrentFragmentIndex = 0;
				break;
			}
			}
		}
		FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(animId, 0);
		
		transaction.replace(R.id.container, fragment).commitAllowingStateLoss();
		
		View categoryImg = activity.findViewById(R.id.main_id_category_img);
		View markImg = activity.findViewById(R.id.main_id_mark);
		TextView categoryTips = (TextView) activity.findViewById(R.id.main_id_category_tips);
		
		if (mCurrentFragmentIndex == 0) {//到这里
			categoryImg.setBackgroundResource(R.drawable.category_games);
			markImg.setBackgroundResource(R.drawable.mark_bg_left);
			categoryTips.setText(R.string.category_games_tips);
			//Games
		} else if (mCurrentFragmentIndex == 2) {//一开始往右跳到这里
			categoryImg.setBackgroundResource(R.drawable.category_medias);
			markImg.setBackgroundResource(R.drawable.mark_bg_right);
			categoryTips.setText(R.string.category_medias_tips);
			//Videos
		} else {//一开始往左
			categoryImg.setBackgroundResource(R.drawable.category_home);
			markImg.setBackgroundResource(R.drawable.mark_bg_center);
			categoryTips.setText(R.string.category_home_tips);
			//Home
		}

	}
	
	public static void translateFragmentToDefault1(Activity activity) {
		View categoryImg = activity.findViewById(R.id.main_id_category_img);
		View markImg = activity.findViewById(R.id.main_id_mark);
		TextView categoryTips = (TextView) activity.findViewById(R.id.main_id_category_tips);
		//小房子图标
		categoryImg.setBackgroundResource(R.drawable.category_home);
		//底部导航
		markImg.setBackgroundResource(R.drawable.mark_bg_center);
		//R.string.category_home_tips=home
		categoryTips.setText(R.string.category_home_tips);
		Fragment fragment = new MainFragment();
		mCurrentFragmentIndex=1;
		//FragmentTransaction管理
		FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
		//设置fragment并commit
		transaction.replace(R.id.container, fragment).commitAllowingStateLoss();	
	}
	
	public static Bitmap createBitmap(Resources res, Bitmap icon, Bitmap background) {
		if (icon == null || background == null)
			return null;

		int iconWidth = icon.getWidth();
		int iconHeight = icon.getHeight();
		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		Canvas cv = new Canvas(background);
		cv.drawBitmap(icon, (float) ((bgWidth - iconWidth) / 2), (float) ((bgHeight - iconHeight) / 2), null);

		return background;
	}

	public static Bitmap createBitmapShadow(Resources res, Bitmap icon, Bitmap background, int iconW, int iconH, int bgW, int bgH) {
		if (icon == null || background == null)
			return null;

		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		Canvas cv = new Canvas(background);
		cv.drawBitmap(icon, 0, 0, null);

		return background;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static void insertShotcut(Context context, String componentName, String category) {
		Shortcut shotcut = new Shortcut();
		shotcut.setCategory(category);
		shotcut.setComponentName(componentName);
		DBHelper db = DBHelper.getInstance(context);
		db.insert(shotcut);
	}

	public static void copyDatabaseFromAssert(Context context, int newDatabaseVersion) {
		Logger.log(Logger.TAG_COPY, "-------Util.copyDatabaseFromAssert() start");
		try {
			InputStream is = context.getAssets().open(Data.DB_FILE);
			byte[] buffer = new byte[1024];
			int count = 0;
			//根据给定条件连接数据库，如果此数据库不存在，则创建
			//建立输出流，写入数据库
			FileOutputStream out = new FileOutputStream(context.openOrCreateDatabase(Data.DB_FILE, Context.MODE_PRIVATE, null).getPath());
			while ((count = is.read(buffer)) > 0) {
				out.write(buffer, 0, count);
				out.flush();
			}
			out.close();
			is.close();
			setInt(context, Data.PRE_DB_VERSION, newDatabaseVersion);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		Logger.log(Logger.TAG_COPY, "-------Util.copyDatabaseFromAssert() completed");
	}

	// 把图片转换成字节数组以便存储在数据库中
	public static byte[] bitmap2bytes(Bitmap bitmap) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		return os.toByteArray();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE);
		return sp.getString(key, "empty");
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE).edit();
		editor.putString(key, value).apply();
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE).edit();
		editor.putInt(key, value).apply();
	}

	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, value).apply();
	}

	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(Data.PRE_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}
	//日期显示
	public static void updateDateDisplay(Activity activity) {
		TextView dateView = (TextView) (activity.findViewById(R.id.center_id_date));
		View timeView = activity.findViewById(R.id.center_id_time);
		TextView weekView = (TextView) (activity.findViewById(R.id.center_id_week));
		if (dateView == null || timeView == null)
			return;
		Date now = new Date();
		if (!mSynchronized) {
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date base = df.parse("2000-11-12 15:21");
				if (now.getTime() > base.getTime()) {
					mSynchronized = true;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (mSynchronized) {
			dateView.setText((DateFormat.getDateInstance(DateFormat.DEFAULT).format(now)));
			dateView.setVisibility(View.VISIBLE);
			timeView.setVisibility(View.VISIBLE);
			GregorianCalendar date = new GregorianCalendar();
			String week = activity.getResources().getStringArray(R.array.week)[(date.get(GregorianCalendar.DAY_OF_WEEK) - 1)];
			weekView.setText(week);
		} else {
			dateView.setVisibility(View.INVISIBLE);
			timeView.setVisibility(View.INVISIBLE);
			weekView.setText(activity.getResources().getString(R.string.sync_date));

		}
	}

	public static void writeToExternalStoragePublic(Context context, String filename, byte[] content) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();

		if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				file.mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper Method to Test if external Storage is Available
	 */
	public static boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	public static boolean isExternalStorageReadOnly() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			state = true;
		}
		return state;
	}
	//重新获取数据库版本 
	public static int getNewDatabaseVersion(Context context, String metaKey) {
		Bundle metaData = null;
		int newVersion = 0;
		if (context == null || metaKey == null) {
			return 0;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				newVersion = metaData.getInt(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return newVersion;
	}

	//是否是有效的系统
	public static boolean isValidSystem() {
		boolean valid = false;
		BufferedReader br;
		try {
			
			br = new BufferedReader(new FileReader("/system/build.prop"));
			String oneLine = br.readLine();// 一次读入一行，直到读入null为文件结束
			while (oneLine != null) {
				if (oneLine.startsWith("ro.vendor.sw.version")) {
					//如果字符串包含k4则返回true anndroid4.4
					valid = oneLine.contains("k4");
					if (valid==true) {
						break;
					} 
					valid = oneLine.contains("L1");
					if (valid==true) {
						break;
					} 
					valid = oneLine.contains("M0");
					if (valid==true) {
						break;
					} 
				} 	 
				oneLine = br.readLine(); // 接着读下一行
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valid;
	}

	//判断平台
	public static String getPlatform() {
		String platform = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("/system/build.prop"));
			String oneLine = br.readLine();// 一次读入一行，直到读入null为文件结束
			while (oneLine != null) {
				if (oneLine.startsWith("ro.board.platform")) {
					if (oneLine.contains("rk3288")) {
						platform = Data.BRAND_RK;
					} else if (oneLine.contains("meson8") || oneLine.contains("gxbaby") || oneLine.contains("gxl")) {
						platform = Data.BRAND_AML;
					} else if (oneLine.contains("jaws") || oneLine.contains("dolphin")) {
						platform = Data.BRAND_AW;
					} else if (oneLine.contains("kylin")) {
						platform = Data.BRAND_RTL;
					} else {
						platform = "unknow";
					}
					break;
				}
				oneLine = br.readLine(); // 接着读下一行
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return platform;
	}
	//通过PackageManager的api 查询已经安装的apk ，返回app列表,获取启动activity信息
	public static List<ResolveInfo> getAllApps(PackageManager pm) {
		//查询Android系统的所有具备ACTION_MAIN和CATEGORY_LAUNCHER
	     // 的Intent的应用程序
		Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
		mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		List<ResolveInfo> apps = pm.queryIntentActivities(mainintent, 0);
		return apps;
	}

}
