package com.netxeon.lignthome.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netxeon.lignthome.R;

public class MemoryCleaner {
	private Activity mActivity;
	private TextView used;
	private Animation operatingAnim;
	private ImageView mAnimImage;
	private Timer mTimer;

	public MemoryCleaner(Activity activity) {
		mActivity = activity;
		initViews();
		operatingAnim = AnimationUtils.loadAnimation(activity, R.anim.memory_cleaner_recircle);
		operatingAnim.setInterpolator(new LinearInterpolator());
		mTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				animHandler.sendEmptyMessage(2);
			}
		};
		try {
			mTimer.schedule(task, 300, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initViews() {
		used = (TextView) mActivity.findViewById(R.id.center_id_memory_used);
		mAnimImage = (ImageView) mActivity.findViewById(R.id.center_id_memory_circle);
	}

	private void updateMemory() {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		long total = getTotalMemory();
		long available = getAvailMemory();
		Logger.log(Logger.TAG_MEMORY, "memory info: total " + total + "    available "+ available);
		String result = numberFormat.format((float) (total - available) / (float) total * 100);
		if (used != null) {
			used.setText(" " + result + "%");
		}

	}

	private Handler animHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				updateMemory();
				stopAnim();
				Toast.makeText(mActivity, R.string.clear_memory_done, Toast.LENGTH_LONG).show();
				break;

			case 1:
				showAnim();
				break;

			case 2:
				updateMemory();
				break;
			}
		}
	};

	public void cleanMemory() {
		new Thread() {
			@Override
			public void run() {
				animHandler.sendEmptyMessage(1);
				ActivityManager activityManger = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
						String[] pkgList = apinfo.pkgList;
						if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
							for (int j = 0; j < pkgList.length; j++) {
								activityManger.killBackgroundProcesses(pkgList[j]);
							}
						}
						animHandler.sendEmptyMessageDelayed(2, 100);
					}
				}
				animHandler.sendEmptyMessage(0);
			}
		}.start();

	}

	private void showAnim() {
		Logger.log(Logger.TAG_MEMORY, " memoryCleaner.showAnim() ");
		if (mAnimImage != null)
			mAnimImage.startAnimation(operatingAnim);
	}

	private void stopAnim() {
		if (mAnimImage != null)
			mAnimImage.clearAnimation();
	}

	private long getAvailMemory() {
		ActivityManager am = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / (1024 * 1024);
	}

	private long getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			Logger.log(Logger.TAG_MEMORY, "line1: " + str2 + "   arr1: " + arrayOfString[1]);
			initial_memory = Long.valueOf(arrayOfString[1]);
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return initial_memory / (1024);
	}

}
