package com.netxeon.lignthome.data;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.ImageView;

import com.netxeon.lignthome.R;
import com.netxeon.lignthome.util.Logger;
import com.netxeon.lignthome.util.Util;

public class NetworkChangedReceiver extends BroadcastReceiver {
	private Activity mContext;

	public NetworkChangedReceiver(Activity context) {
		mContext = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION) || intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			initNetwork();
		}
		
		Util.updateDateDisplay(mContext);
		
	}

	/*
	 * update networkIcon while start app or network changed
	 */
	private void initNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		boolean ethernet = false;
		boolean available = false;
		if (info != null) {
			ethernet = info.getType() == ConnectivityManager.TYPE_ETHERNET;
			available = connManager.getActiveNetworkInfo().isConnected();
		}
		Logger.log(Logger.TAG_WEATHER, "NetworkChangedReceiver.initNetwork() network available? " + available);
		
		ImageView wifi_image = (ImageView) mContext.findViewById(R.id.main_id_wifi);
		if (ethernet) {
			wifi_image.setImageResource(available ? R.drawable.ethernet : R.drawable.wifi_signl1);
		} else {
			WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			int level = WifiManager.calculateSignalLevel(manager.getConnectionInfo().getRssi(), 5);
			wifi_image.setImageResource(R.drawable.wifi_signl);
			switch (level) {
			case 1:
				wifi_image.setImageLevel(level);
				break;
			case 2:
				wifi_image.setImageLevel(level);
				break;
			case 3:
				wifi_image.setImageLevel(level);
				break;
			case 4:
			case 5:
				wifi_image.setImageLevel(4);
				break;
			default:
				wifi_image.setImageLevel(1);
			}
		}
	}
}
