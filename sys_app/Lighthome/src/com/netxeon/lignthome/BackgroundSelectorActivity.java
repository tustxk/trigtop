package com.netxeon.lignthome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.netxeon.lignthome.data.Data;

public class BackgroundSelectorActivity extends Activity {
	private final int[] mBackgroundsSmall = {R.drawable.bg_black_small, R.drawable.bg_fg_small, R.drawable.bg_middle_small, R.drawable.bg_white_small};
	private final int[] mBackgrounds = {R.drawable.bg_black, R.drawable.bg_fg, R.drawable.bg_middle, R.drawable.bg_white};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_bg_selector);
		initViews();
	}
	
	private void initViews(){
		GridView gridview = (GridView) findViewById(R.id.bg_id_gridview);
		gridview.setAdapter(new BackgroundAdapter(BackgroundSelectorActivity.this));
		gridview.setOnItemClickListener(new ItemClickListener());
		gridview.requestFocus();
	}
	

	private class BackgroundAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public BackgroundAdapter(Context context){
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mBackgrounds.length;
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
				convertView = mInflater.inflate(R.layout.bg_item, null);
				holder = new ViewHolder();
				holder.img = (ImageView)convertView.findViewById(R.id.bg_id_item);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.img.setBackgroundResource(mBackgroundsSmall[position]);

			return convertView;
		}
		
		private class ViewHolder{
			ImageView img;
		}
	}

	/*
	 * handle the icon click event
	 */
	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			int id = mBackgrounds[position];
			Intent intent = new Intent();
			intent.putExtra(Data.PRE_BACKGROUND, id);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
