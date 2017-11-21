package com.netxeon.lignthome;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.netxeon.lignthome.data.DBHelper;
import com.netxeon.lignthome.data.Data;
import com.netxeon.lignthome.data.Shortcut;
import com.netxeon.lignthome.data.ShortcutsAdapter;
import com.netxeon.lignthome.data.ShortcutsAdapter.ShortcutHolder;
import com.netxeon.lignthome.util.Logger;

public class CategoryFragment extends Fragment {
	private PackageManager pm;
	private List<Shortcut> mShotcutList;
	private ShortcutsAdapter mAdapter;
	private Activity mActivity;
	private String mCategory;
	private UpdateShortcutsReceiver mUpdateShortcutsReceiver;
	private IntentFilter mIntentFilter;
	private DBHelper mDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCategory = getArguments().getString("category", "games");
		Logger.log(Logger.TAG_SHORTCUT, "CategoryFragment.onCreate() current: " + mCategory);
		mActivity = getActivity();
		//广播接收器
		mUpdateShortcutsReceiver = new UpdateShortcutsReceiver();
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Data.ACTION_UPDATE_SHORTCUTS);//过滤器
		
		pm = mActivity.getPackageManager();
		mDB = DBHelper.getInstance(mActivity);//获取dbhelper对象
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		initData();
		getActivity().registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mUpdateShortcutsReceiver);
		super.onResume();
	}

	private void initData() {
		mShotcutList = mDB.queryByCategory(mCategory);
		GridView gridview = (GridView) mActivity.findViewById(R.id.fragment_category_id_gridview);
		Shortcut forAddItem = new Shortcut();
		forAddItem.setComponentName(Data.ADDITIONAL);
		mShotcutList.add(forAddItem);
		mAdapter = new ShortcutsAdapter(mActivity, mShotcutList, pm, false);
		gridview.setAdapter(mAdapter);
		gridview.setOnItemClickListener(new ItemClickListener());
		gridview.requestFocus();
	}

	/*
	 * handle the icon click event
	 */
	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			ShortcutHolder holder = (ShortcutHolder) arg1.getTag();
			ComponentName componentName = holder.componentName;
			
			if (Data.ADDITIONAL.equals(componentName.getPackageName())) {
				Intent editIntent = new Intent(mActivity, AppsActivity.class);
				editIntent.putExtra("category", mCategory);
				startActivity(editIntent);
			} else {
				try {
					Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
					mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
					mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					mainintent.setComponent(componentName);
					startActivity(mainintent);
				} catch (Exception e) {
					Log.e("error", "DetailsActivity.ItemClickListener.onItemClick() startActivity failed: " + componentName);
				}
			}

		}
	}

	private class UpdateShortcutsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.log(Logger.TAG_PACKAGE, "CategoryFragment.UpdateShortcutsReceiver.onReceive() update shortcut");
			initData();

		}

	}
}
