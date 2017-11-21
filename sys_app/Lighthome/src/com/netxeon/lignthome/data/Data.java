package com.netxeon.lignthome.data;

import java.util.HashSet;
import java.util.Set;

import com.netxeon.lignthome.R;
//数据，常量等定义的类
public class Data {
	public static final String DB_FILE = "netxeon.db";
	public static final String PRE_FILE = "prefile";
	public static final String ACTION_UPDATE_SHORTCUTS = "com.netxeon.lighthome.UPDATE_SHORTCUTS";
	public static final String PRE_DATA_INITIALIZED = "data_initialized";
	public static final String PRE_DB_VERSION = "db_version";
	public static final String PRE_BACKGROUND = "background";
	public static final String ADDITIONAL = "additional";
	
	public static final String BRAND_RK = "Rockchip";
	public static final String BRAND_AML = "Amlogic";
	public static final String BRAND_AW = "Allwinner";
	public static final String BRAND_RTL = "Realtek";
	
	private static final int[] mWeatherIcons = {
		R.drawable.weather0, R.drawable.weather1, R.drawable.weather2, R.drawable.weather3, R.drawable.weather4,
		R.drawable.weather5, R.drawable.weather6, R.drawable.weather7, R.drawable.weather8, R.drawable.weather9,
		R.drawable.weather10, R.drawable.weather11, R.drawable.weather12, R.drawable.weather13, R.drawable.weather14,
		R.drawable.weather15, R.drawable.weather16, R.drawable.weather17, R.drawable.weather18, R.drawable.weather19,
		R.drawable.weather20, R.drawable.weather21, R.drawable.weather22, R.drawable.weather23, R.drawable.weather24,
		R.drawable.weather25, R.drawable.weather26, R.drawable.weather27, R.drawable.weather28, R.drawable.weather29,
		R.drawable.weather30, R.drawable.weather31, R.drawable.weather32, R.drawable.weather33, R.drawable.weather34,
		R.drawable.weather35, R.drawable.weather36, R.drawable.weather37, R.drawable.weather38, R.drawable.weather39,
		R.drawable.weather40, R.drawable.weather41, R.drawable.weather42, R.drawable.weather43, R.drawable.weather44,
		R.drawable.weather45, R.drawable.weather46, R.drawable.weather47};

	public final static class Category {
		public final static String CATEGORY = "category";
		public final static String MEDIA = "media";
		public final static String GAME = "game";
		public final static String FAVORITE = "favorite";
	};
	
	public final static Set<Integer> getCenterIconIds() {
		HashSet<Integer> idSet = new HashSet<Integer>();
		idSet.add(R.id.center_id_browser);
		idSet.add(R.id.center_id_file);
		idSet.add(R.id.center_id_market);
		idSet.add(R.id.center_id_xbmc);
		idSet.add(R.id.center_id_setting);
		//idSet.add(R.id.center_id_eshare);
		//idSet.add(R.id.center_id_flix);
		//idSet.add(R.id.main_id_search);
		return idSet;
	};
	
	public final static int getWeatherIcon(int code){
		return mWeatherIcons[code];
	}
}
