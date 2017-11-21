package com.netxeon.lignthome.weather;

import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeather.SEARCH_MODE;
import android.app.Activity;
import android.os.Handler;

import com.netxeon.lignthome.util.Logger;

public class WeatherUtils {

	public static final String WEATHER_CITY = "locationCity";
	public static final int MSG_WEATHER_OK = 1;
	public static final int MSG_WEATHER_NO_CITY = 0;
	public static final int MSG_WEATHER_FAILED = -1;
	public static final int MSG_WEATHER_PARSE_CITY_FAILED = -2;
	public static final int MSG_WEATHER_NETWORK_DISCONNECTED = -3;
	
	public static void updateWeatherByCityName(Activity activity, Handler weatherHandler, String city){
		if (!city.equals("empty")) {
			YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, Logger.DEBUG_WEATHER);
			mYahooWeather.setNeedDownloadIcons(false);
			mYahooWeather.setSearchMode(SEARCH_MODE.PLACE_NAME);
			mYahooWeather.queryYahooWeatherByPlaceName(activity,
					city, new WeatherUpdater(weatherHandler));
		} else {
			weatherHandler.sendEmptyMessage(MSG_WEATHER_NO_CITY);
		}
		
	}
	
	public static void updateWeatherByGPS(Activity activity, Handler weatherHandler){
		YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, Logger.DEBUG_WEATHER);
		mYahooWeather.setNeedDownloadIcons(false);
		mYahooWeather.setSearchMode(SEARCH_MODE.GPS);
		mYahooWeather.queryYahooWeatherByGPS(activity, new WeatherUpdater(weatherHandler));
		
	}
	
}
