package com.netxeon.lignthome;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.netxeon.lignthome.data.DBHelper;
import com.netxeon.lignthome.data.Data;
import com.netxeon.lignthome.data.IconItem;
import com.netxeon.lignthome.data.Shortcut;
import com.netxeon.lignthome.util.Logger;
import com.netxeon.lignthome.util.Util;

/*
 * this activity will display two mode:
 * 1. It's function is just like AllApps view, so that user can launch an application.
 * 2. Let user select their preferred application and display it on shortcut view.
 */
public class AppsActivity extends Activity {

	private PackageManager pm;
	private List<IconItem> mPackages;
	private AppsAdapter mAdapter;
	private LayoutInflater mInflater;
	// fitter mode means we are in selection mode,
	// that when the user click the icon, the icon will become selected
	private boolean mFitterMode;
	private String mCurrentCategory;
	private DBHelper mDB;
	private static final int MSG_DELETE = -1;
	private static final int MSG_INSERT = 1;
	private static final int MSG_INIT_DATA = 2;
	private static final int MSG_REFLASH = 3;
	private PackageChangedReceiver mUpdateShortcutsReceiver;
	private IntentFilter mIntentFilter;
	private ComponentName mLongClickedComponentName;
	private boolean mIsSystemApp;
	private ProgressDialog mWait_dialog;
	private GridView mGridView;

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_all_apps);
		//findViewById(R.id.apps_id_root).setBackgroundResource(Util.getInt(AppsActivity.this, Data.PRE_BACKGROUND, R.drawable.bg_white));
		Intent argIntent = getIntent();
		mCurrentCategory = argIntent.getStringExtra("category");
		if (mCurrentCategory != null && !mCurrentCategory.isEmpty()) {
			mFitterMode = true;
			mDB = DBHelper.getInstance(this);
		}
		pm = getPackageManager();
		mPackages = new ArrayList<IconItem>();
		sendInitDataMessage();
		mUpdateShortcutsReceiver = new PackageChangedReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mIntentFilter.addDataScheme("package");
		mWait_dialog = ProgressDialog.show(this, "", getString(R.string.load_apps), false);
	}

	/*
	 * do registerReceiver in onStart() method not in onResume() because when
	 * the uninstall dialog display, the activity state change to pause and can
	 * not receive the package changed broadcast! so as unRegisterReceiver().
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
		Logger.log(Logger.TAG_PACKAGE, "AppsActivity onStart() registerReceiver  ");
		super.onStart();
	}

	@Override
	public void onResume() {
		sendInitDataMessage();
		Logger.log(Logger.TAG_PACKAGE, "AppsActivity onResume()  ");
		super.onResume();
	}

	@Override
	public void onStop() {
		unregisterReceiver(mUpdateShortcutsReceiver);
		Logger.log(Logger.TAG_PACKAGE, "AppsActivity onStop() unregisterReceiver  ");
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getScanCode() == 392) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void reflash() {
		Logger.log(Logger.TAG_PACKAGE, "AppsActivity initData() mFitterMode: " + mFitterMode);
		mGridView = (GridView) findViewById(R.id.all_apps_gridview);
		mAdapter = new AppsAdapter(this);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new ItemClickListener());
		mGridView.setOnItemLongClickListener(new ItemLongClickListener());
		mGridView.requestFocus();
		Logger.log(Logger.TAG_PACKAGE, "AppsActivity reflash() mFitterMode: " + mFitterMode);
		if (mFitterMode) {
			fitterThePersistentPackages();
			initSelections();
		}
		mAdapter.notifyDataSetChanged();
		if (mWait_dialog.isShowing())
			mWait_dialog.dismiss();
	}

	// do not display the persistent packages
	private void fitterThePersistentPackages() {
		List<IconItem> persistentPackages = new ArrayList<IconItem>();
		List<Shortcut> persistentShortcut = new ArrayList<Shortcut>();
		persistentShortcut = mDB.queryAllPersistents();
		for (int i = 0; i < persistentShortcut.size(); i++) {
			for (IconItem info : mPackages) {
				info.setVisibility(View.INVISIBLE);
				if (persistentShortcut.get(i).getComponentName().equals(info.getComponentName())) {
					persistentPackages.add(info);
				}
			}
		}
		mPackages.removeAll(persistentPackages);
	}

	private void initSelections() {
		List<Shortcut> selectedList = new ArrayList<Shortcut>();
		selectedList = mDB.queryByCategory(mCurrentCategory);
		for (Shortcut shotcut : selectedList) {
			for (IconItem info : mPackages) {
				if (shotcut.getComponentName().equals(info.getComponentName().toString())) {
					info.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private class AppsAdapter extends BaseAdapter {

		public AppsAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mPackages.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.app_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.app_lable);
				holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
				holder.selectView = (ImageView) convertView.findViewById(R.id.app_selection_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(mPackages.get(position).getLable());
			holder.icon.setBackground(mPackages.get(position).getIcon());
			holder.selectView.setVisibility(mPackages.get(position).getVisibility());
			holder.componentName = mPackages.get(position).getComponentName();
			return convertView;
		}
	}

	/*
	 * the display item
	 */
	private class ViewHolder {
		private TextView text;
		private ImageView icon;
		private ImageView selectView;
		private ComponentName componentName;
	}

	/*
	 * handle the icon click event
	 */
	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			ViewHolder holder = (ViewHolder) arg1.getTag();
			if (mFitterMode) {
				boolean visible = holder.selectView.getVisibility() == View.VISIBLE ? true : false;
				mPackages.get(position).setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
				mAdapter.notifyDataSetChanged();
				Message msg = dbHandler.obtainMessage();
				msg.obj = holder;
				msg.arg1 = visible ? MSG_DELETE : MSG_INSERT;
				msg.sendToTarget();
			} else {
				startApplication(holder.componentName);
			}
		}
	}

	private class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			mLongClickedComponentName = holder.componentName;
			try {
				mIsSystemApp = (pm.getApplicationInfo(mLongClickedComponentName.getPackageName(), 0).flags & ApplicationInfo.FLAG_SYSTEM) != 0;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			showLongClickedDialog();
			return true;
		}

	}

	private void showLongClickedDialog() {
		int arrayId = mIsSystemApp ? R.array.long_click_dialog_items_no_uninstall : R.array.long_click_dialog_items;
		new AlertDialog.Builder(AppsActivity.this).setTitle(getResources().getString(R.string.long_click_dialog_title))
				.setItems(arrayId, new OnDialogItemClickListener() {
				}).show();

	}

	private class OnDialogItemClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			if (mIsSystemApp) {
				switch (which) {
				case 0:
					startApplication(mLongClickedComponentName);
					break;

				case 1:
					showAppDetails(mLongClickedComponentName.getPackageName());
					break;
				}

			} else {
				switch (which) {
				case 0:
					startApplication(mLongClickedComponentName);
					break;
				case 1:
					unInstallApplication(mLongClickedComponentName.getPackageName());
					break;

				case 2:
					showAppDetails(mLongClickedComponentName.getPackageName());
					break;

				}
			}
		}
	}

	private void startApplication(ComponentName componentName) {
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.setComponent(componentName);
			startActivity(intent);
		} catch (Exception e) {
			Log.e("error", "AppsActivity.startApplication() startActivity failed: " + componentName);
		}
	}

	private void showAppDetails(String packageName) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		try {
			startActivity(intent);
		} catch (Exception e) {
			Log.e("error", "AppsActivity.showAppDetails() show details failed: " + packageName);
		}
	}

	private void unInstallApplication(String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + packageName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		try {
			startActivity(intent);
		} catch (Exception e) {
			Log.e("error", "AppsActivity.unInstallApplication() uninstall failed: " + packageName);
		}
	}

	private Handler dbHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == MSG_INSERT) {
				ViewHolder holder = (ViewHolder) msg.obj;
				Util.insertShotcut(AppsActivity.this, holder.componentName.toString(), mCurrentCategory);
			} else if (msg.arg1 == MSG_DELETE) {
				ViewHolder holder = (ViewHolder) msg.obj;
				mDB.deleteShortcut(holder.componentName.toString(), mCurrentCategory);
			} else if (msg.arg1 == MSG_INIT_DATA) {
				new LoadAppsTask().execute();
			} else if (msg.arg1 == MSG_REFLASH) {
				reflash();
			}
		}

	};

	/*
	 * sort the packages based on app's label
	 */
	private class PackagesComparator implements Comparator<IconItem> {

		@Override
		public int compare(IconItem item1, IconItem item2) {
			Collator collator = Collator.getInstance();
			return collator.compare(item1.getLable(), item2.getLable());
		}

	}

	private class PackageChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.log(Logger.TAG_PACKAGE, "AppsActivity.UpdateShortcutsReceiver.onReceive() update application");
			sendInitDataMessage();

		}

	}

	private class LoadAppsTask extends AsyncTask<Void, Void, Integer> {
		private List<IconItem> internalPackages;

		@Override
		protected Integer doInBackground(Void... params) {
			internalPackages = new ArrayList<IconItem>();
			Intent localIntent = new Intent("android.intent.action.MAIN");
			localIntent.addCategory("android.intent.category.LAUNCHER");
			internalPackages.clear();
			List<ResolveInfo> localList = pm.queryIntentActivities(localIntent, 0);
			IconItem item;
			for (ResolveInfo info : localList) {
				item = new IconItem();
				item.setLable(info.activityInfo.loadLabel(pm).toString());
				item.setIcon(info.activityInfo.loadIcon(pm));
				item.setVisibility(View.INVISIBLE);
				item.setComponentName(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
				internalPackages.add(item);
			}
			Collections.sort(internalPackages, new PackagesComparator());
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (internalPackages.size() > 0) {
				mPackages = internalPackages;
				Message msg = dbHandler.obtainMessage();
				msg.arg1 = MSG_REFLASH;
				msg.sendToTarget();
			}
		}
	}

	private void sendInitDataMessage() {
		Message msg = dbHandler.obtainMessage();
		msg.arg1 = MSG_INIT_DATA;
		msg.sendToTarget();
	}
}
