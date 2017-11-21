package com.netxeon.lignthome.data;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netxeon.lignthome.R;
import com.netxeon.lignthome.util.Logger;
import com.netxeon.lignthome.util.Util;

//GridView列表适配器
public class ShortcutsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Shortcut> mShotcutList;
	private List<ResolveInfo> mApps;
	private Context mContext;
	private PackageManager mPm;
	private boolean mIsFavorite;

	public ShortcutsAdapter(Context context, List<Shortcut> shotcutList, PackageManager pm, boolean isFavorite) {
		mContext = context;
		mPm = pm;
		mInflater = LayoutInflater.from(context);
		mShotcutList = shotcutList;
		// 获取到所有app列表可启动的act
		mApps = Util.getAllApps(pm);
		mIsFavorite = isFavorite;
	}

	@Override
	public int getCount() {
		return mShotcutList.size();
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
		ShortcutHolder holder = null;
		if (convertView == null) {
			if (mIsFavorite) {
				convertView = mInflater.inflate(R.layout.favorate_item, null);
				holder = new ShortcutHolder();
				holder.background = (View) convertView.findViewById(R.id.favorite_item);
				holder.text = (TextView) convertView.findViewById(R.id.favorite_lable);
				holder.icon = (ImageView) convertView.findViewById(R.id.favorite_icon);
			} else {
				convertView = mInflater.inflate(R.layout.shortcut_item, null);
				holder = new ShortcutHolder();
				holder.background = (View) convertView.findViewById(R.id.shotcut_item);
				holder.text = (TextView) convertView.findViewById(R.id.shotcut_lable);
				holder.icon = (ImageView) convertView.findViewById(R.id.shotcut_icon);
			}

			convertView.setTag(holder);
		} else {
			holder = (ShortcutHolder) convertView.getTag();
		}
		//传进来的列表
		String componentStr = mShotcutList.get(position).getComponentName();
		
		Logger.log(Logger.TAG_SHORTCUT, "componentStr:" + componentStr + "  position:" + position);
		if (componentStr.equals(Data.ADDITIONAL)) {//添加按钮
			holder.icon.setBackgroundResource(R.drawable.app_icon_more);
			holder.componentName = new ComponentName(componentStr, "");
			holder.text.setText("");
		} else {
			ResolveInfo resolveInfo = null;
			ComponentName componentName = null;
			for (ResolveInfo info : mApps) {
				resolveInfo = null;
				componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
				if (componentName.toString().equals(componentStr)) {
					resolveInfo = info;
					break;
				}
			}
			if (resolveInfo == null) {
				// this situation may happen at run first time: we add an
				// shortcut while build this app but the system not preinstall
				// it.
				Log.e("error", "package not install?? " + componentStr);
				DBHelper.getInstance(mContext).deleteByPackageName(componentStr);
				mContext.sendBroadcast(new Intent(Data.ACTION_UPDATE_SHORTCUTS));
			} else {
				holder.text.setText(resolveInfo.activityInfo.loadLabel(mPm));
				holder.icon.setBackground(resolveInfo.loadIcon(mPm));
				holder.componentName = componentName;
			}

		}

		return convertView;
	}

	/*
	 * the display item
	 */
	public class ShortcutHolder {
		public View background;
		public TextView text;
		public ImageView icon;
		public ComponentName componentName;//封装一个组件的应用包名和组件的名字。
	}

}