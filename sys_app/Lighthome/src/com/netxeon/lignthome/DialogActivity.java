package com.netxeon.lignthome;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.example.testroundprogressbar.RoundProgressBar;
import com.netxeon.lignthome.R.layout;
import com.netxeon.lignthome.circleprogress.DonutProgress;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;



public class DialogActivity extends Activity {

	RelativeLayout relativeLayout1;
	RelativeLayout relativeLayout2;
	RelativeLayout relativeLayout3;

	private View rootview;

	// DonutProgress donut_progress2;
	private RoundProgressBar mProgress1, mProgress2, mProgress3;

	private final int requestCode = 1;
	private volatile boolean exit = false;

	// msg tag
	private static final int UPDATE_CODE = 1;
	private static final int END_CODE = 2;
	private static final int UPDATE_CODE_delay = 3;
	int i = 0;

	// MytimeTask2 task2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.dialogactivity);
		
		initData();
		rootview = this.getWindow().getDecorView();
		int aaa = rootview.findFocus().getId();
		Log.d("bo","aaa = "+aaa);
		switch(aaa){
			case R.id.line1:
				Log.d("bo","ly line1");
				break;
			case R.id.line2:
				Log.d("bo","ly line2");
				break;
			case R.id.line3:
				Log.d("bo","ly line3");
				break;
			
		}
		// donut_progress2 = (DonutProgress) findViewById(R.id.donut_progress2);


//		WindowManager manager = getWindowManager();
//		Display display = manager.getDefaultDisplay();
//		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.height = (int) (display.getHeight() * 1.0); // 高度设置为屏幕的1.0
//		p.width = (int) (display.getWidth() * 1.0); // 宽度设置为屏幕的1.0
//		getWindow().setAttributes(p); // 设置生效

	}

	@Override
	protected void onDestroy() {

		// if (change==1) {
		// timer.cancel();
		// timer=null;
		// Log.i("test", "ondestroy");
		// }

		super.onDestroy();
	}

	private void initData() {

		relativeLayout1 = (RelativeLayout) findViewById(R.id.line1);
		relativeLayout2 = (RelativeLayout) findViewById(R.id.line2);
		relativeLayout3 = (RelativeLayout) findViewById(R.id.line3);
		relativeLayout1.setOnClickListener(onClickListener);
		relativeLayout2.setOnClickListener(onClickListener);
		relativeLayout3.setOnClickListener(onClickListener);

		relativeLayout1.setOnFocusChangeListener(onFocusChangeListener);
		relativeLayout2.setOnFocusChangeListener(onFocusChangeListener);
		relativeLayout3.setOnFocusChangeListener(onFocusChangeListener);

		mProgress1 = (RoundProgressBar) findViewById(R.id.roundProgressBar1);
		mProgress2 = (RoundProgressBar) findViewById(R.id.roundProgressBar2);
		mProgress3 = (RoundProgressBar) findViewById(R.id.roundProgressBar3);
		
		mProgress1.setCenterText(getResources().getString(R.string.poweroff));
		mProgress2.setCenterText(getResources().getString(R.string.sleep));
		mProgress3.setCenterText(getResources().getString(R.string.Delay));
		relativeLayout2.setFocusable(true);
		relativeLayout2.setFocusableInTouchMode(true);
		relativeLayout2.requestFocus();
		relativeLayout2.requestFocusFromTouch();

//		mProgress1.setTextSize(20);
//		mProgress2.setTextSize(20);
//		mProgress3.setTextSize(20);
//		mProgress3.setMax(60);
//		Log.i("bo", curprocess + "");
//		mProgress3.setProgress(curprocess);

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.line1:
				Log.i("bo", "line1");
						
						//Log.d("bo","aaa = "+rootview.findFocus().getId());
		switch(rootview.findFocus().getId()){
			case R.id.line1:
				Log.d("bo","ly line1");
				
				break;
			case R.id.line2:
				Log.d("bo","ly line2");
				//relativeLayout2.requestFocus();
		//relativeLayout2.requestFocusFromTouch();
				break;
			case R.id.line3:
				Log.d("bo","ly line3");
				//relativeLayout3.requestFocus();
		//relativeLayout3.requestFocusFromTouch();
				break;
			
		}
				try {
					// 关机
					shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case R.id.line2:
				Log.i("bo", "按键按下");

				//		Log.d("bo","aaa = "+rootview.findFocus().getId());
		switch(rootview.findFocus().getId()){
			case R.id.line1:
				Log.d("bo","ly line1");
				//relativeLayout1.requestFocus();
		//relativeLayout1.requestFocusFromTouch();
				break;
			case R.id.line2:
				Log.d("bo","ly line2");
				
				break;
			case R.id.line3:
				Log.d("bo","ly line3");
				//relativeLayout3.requestFocus();
		//relativeLayout3.requestFocusFromTouch();
				break;
			
		}
				// 休眠
				//relativeLayout2.requestFocus();
				//relativeLayout2.requestFocusFromTouch();
				sleep5s();
				break;
			case R.id.line3:
				// 延时休眠

				//if (curprocess == 0) {
				//	pause();
				//}
				//delaysleep();

						//Log.d("bo","aaa = "+rootview.findFocus().getId());
		switch(rootview.findFocus().getId()){
			case R.id.line1:
				Log.d("bo","ly line1");
				//relativeLayout1.requestFocus();
		//relativeLayout1.requestFocusFromTouch();
				break;
			case R.id.line2:
				Log.d("bo","ly line2");
				//relativeLayout2.requestFocus();
		//relativeLayout2.requestFocusFromTouch();
				break;
			case R.id.line3:
				Log.d("bo","ly line3");
				
				break;
			
		}
				try{
					reboot();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}

	};

	/*private void delaysleep() {

		// 拿进度定时休眠
		// task2 = new MytimeTask2();
		// task2.start();
		// ActivityManager am = (ActivityManager)
		// getSystemService(ACTIVITY_SERVICE);
		// ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		// Log.d("", "pkg:"+cn.getPackageName());
		// Log.d("", "cls:"+cn.getClassName());

		setSleep();
		// Intent intent = new Intent(this,cn.getClass());
		// startActivity(intent);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		moveTaskToBack(true);

	}*/

	// private void delaysbreak() {
	// if (task2 != null) {
	// task2.interrupt();
	// // task2.stop();
	// Log.i("test", "求终止");
	// }
	// }

	// 线程
	// class MytimeTask2 extends Thread {
	// @Override
	// public void run() {
	//
	// for (int i = 0; i < curprocess * 60; i++) {
	//
	//
	// Log.i("test", exit + "exit");
	// try {
	// Thread.sleep(1000);
	// Log.i("test", "延时" + curprocess);
	// } catch (InterruptedException e) {
	//
	// e.printStackTrace();
	// Thread.currentThread().interrupt();
	// Log.i("test", "break for sleep");
	// break;
	// }
	// }
	//
	// if (!Thread.currentThread().isInterrupted()) {
	// // 休眠
	// pause();
	// }
	//
	// }
	// }

	private void sleep5s() {
		// 设置定时器
		// 设置定时器任务
		// timer = new Timer();
		// timer.schedule(new MytimeTask(), 0, 1000);
		// 开线程
		new MytimeThread().start();

	}

	// 进度条2的线程
	class MytimeThread extends Thread {

		@Override
		public void run() {
			// 设置发消息,//接收新ui
			for (int i = 1; i < 102; i++) {

				try {
					handler.sendEmptyMessage(UPDATE_CODE);
					Log.i("bo", "发消息");
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!relativeLayout2.isFocused()) {

					handler.sendEmptyMessage(END_CODE);
					break;
				}
			}

		}
	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == UPDATE_CODE) {
				if (mProgress2.getProgress() < 100) {
					mProgress2.setProgress(i + 1);
					Log.i("bo", mProgress2.getProgress() + "");
					Log.i("bo", "更新ui--圈2");
					i += 1;

				} else {
					i = 0;
					Log.i("bo", "坐等休眠");
					// 休眠函数
					pause();
				}
			} else if (msg.what == END_CODE) {
				mProgress2.setProgress(0);
				i = 0;

			} else if (msg.what == UPDATE_CODE_delay) {
				pause();

			}

		}
	};

	static int time = 0, flag = 0, change = 0;
	Timer timer;
	TimerTask task;

	// 初始时间
	public void resetOrInitTimer(int delay) {

		// 第一次进去
		if (flag == 0) {
			time = delay;
		} else {
			change = 1;
			if (timer == null) {
				Log.i("test", "定时器空");
			}

			if (timer != null) {
				timer.cancel();
				timer = null;
				Log.i("test", "cancel");
			}
			time = delay;
			flag = 0;

		}

//		Toast.makeText(getApplicationContext(), delay + "s", Toast.LENGTH_SHORT)
//				.show();
	}

	// 开始睡眠
	public void setSleep() {
		if (time == 0) {
			pause();
		} else {

			if (flag == 0) {
				timer = new Timer();
				task = new TimerTask() {

					@Override
					public void run() {

						Message message = new Message();
						message.what = UPDATE_CODE_delay;
						Log.i("bo", "消息---时间的流逝");
						handler.sendEmptyMessage(message.what);
						Log.i("bo", "消息---时间的流逝");

					}
				};
//				Toast.makeText(getApplicationContext(), "将在" + time + "秒后关机",
//						Toast.LENGTH_SHORT).show();

				timer.schedule(task, time * 1000);// 注意毫秒
				flag = 1;
				//finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"timer is still running", Toast.LENGTH_SHORT).show();
			}

		}
	}

	// 左右改变焦点
	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.line1:
				if (hasFocus) {
					Log.i("test", hasFocus + "焦点");

					// 改变球边框颜色
					int mycolor = getResources().getColor(
							R.color.text_color_blue);
					mProgress1.setCricleColor(mycolor);
					mProgress1.setTextColor(mycolor);

				} else {
					int mycolor = getResources().getColor(
							R.color.dimgray);
					mProgress1.setCricleColor(mycolor);
					mProgress1.setTextColor(mycolor);
					Log.i("test", "line1 un focus");
				}

				break;
			case R.id.line2:
				if (hasFocus) {
					Log.i("test", hasFocus + "");

					// 改变球边框颜色
					int mycolor = getResources().getColor(
							R.color.text_color_blue);
					mProgress2.setCricleColor(mycolor);
					mProgress2.setTextColor(mycolor);

				} else {
					Log.i("test", "line2 un focus");
					//int mycolor = getResources().getColor(
					//		R.color.text_color_gray);
					int mycolor = getResources().getColor(
							R.color.dimgray);
					mProgress2.setCricleColor(mycolor);
					mProgress2.setTextColor(mycolor);

					// 失去焦点发结束消息
					handler.sendEmptyMessage(END_CODE);
				}

				break;
			case R.id.line3:
				if (hasFocus) {
					Log.i("test", hasFocus + "");

					// 改变球边框颜色
					int mycolor = getResources().getColor(
							R.color.text_color_blue);
					mProgress3.setCricleColor(mycolor);
					mProgress3.setTextColor(mycolor);

				} else {
					Log.i("test", "line3 un focus");
					int mycolor = getResources().getColor(
							R.color.dimgray);
					mProgress3.setCricleColor(mycolor);
					mProgress3.setTextColor(mycolor);
				}

				break;

			default:
				break;
			}

		}
	};

	// 上下选择
/*	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:// 按向上键

			Log.i("test", "上键");
			moveUp();
			resetOrInitTimer(curprocess * 60);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:// 按向下键
			Log.i("test", "下键");

			moveDown();
			resetOrInitTimer(curprocess * 60);

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	};

	static int cur = 0;

	static int curprocess;

	private void moveUp() {
		Log.i("test", "up");

		if (relativeLayout3.isFocused()) {
			if (cur < 4) {
				cur++;
				curprocess = cur * 15;
				mProgress3.setProgress(cur * 15);
				mProgress3.setCenterText(cur * 15 + "min");

			} else {
				Toast.makeText(this, "超出范围", 2000).show();
			}
		}

	}

	private void moveDown() {

		Log.i("test", "down");
		if (relativeLayout3.isFocused()) {
			if (cur > 0) {
				cur--;
				curprocess = cur * 15;
				mProgress3.setProgress(cur * 15);
				mProgress3.setCenterText(cur * 15 + "min");

			} else {
				Toast.makeText(this, "超出范围", 2000).show();
			}
		}

	}*/

	// 关机
	private void shutdown() throws IOException {


		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pm.shutdown(true,false);
		finish();
	}

	private void reboot() throws IOException {

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pm.reboot(null);
		finish();
	}

	// 休眠函数
	protected void pause() {
		/**
		 * 锁屏并关闭屏幕
		 */
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pm.goToSleep(SystemClock.uptimeMillis());
		finish();
	//	policyManager.lockNow();

	}

}
