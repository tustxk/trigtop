package com.netxeon.lignthome.weather;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;
import android.os.Handler;
import android.os.Message;

import com.netxeon.lignthome.util.Logger;

public class WeatherUpdater implements YahooWeatherInfoListener,
		YahooWeatherExceptionListener {
	private Handler mWeatherHandler;

	public WeatherUpdater(Handler weatherHandler) {
		mWeatherHandler = weatherHandler;
	}

	@Override
	public void onFailConnection(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_NETWORK_DISCONNECTED);
		Logger.log(Logger.TAG_WEATHER, "WeatherUpdater.gotWeatherInfo() onFailConnection");

	}

	@Override
	public void onFailFindLocation(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_PARSE_CITY_FAILED);
		Logger.log(Logger.TAG_WEATHER, "WeatherUpdater.gotWeatherInfo() onFailFindLocation");

	}

	@Override
	public void onFailParsing(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_FAILED);
		Logger.log(Logger.TAG_WEATHER, "WeatherUpdater.gotWeatherInfo() onFailParsing");

	}

	@Override
	public void gotWeatherInfo(WeatherInfo info) {
		Message msg = new Message();
		msg.what = WeatherUtils.MSG_WEATHER_OK;
		msg.obj = info;
		if(info == null){
			Logger.log(Logger.TAG_WEATHER, "WeatherUpdater.gotWeatherInfo() weather info is null");
		}else{
			Logger.log(Logger.TAG_WEATHER, "WeatherUpdater.gotWeatherInfo() weather info get succes");
			mWeatherHandler.sendMessage(msg);
		}

	}

}
