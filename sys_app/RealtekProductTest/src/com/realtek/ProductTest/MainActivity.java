package com.realtek.ProductTest;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.net.NetworkInterface;
//import java.net.NetworkInterface.SocketException;

import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.Build;

import android.widget.CheckBox;
import android.net.Uri;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Environment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.graphics.PixelFormat;

import java.util.Arrays;
import android.os.storage.StorageManager;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Toast;
import com.android.internal.util.MemInfoReader;
import android.media.MediaRecorder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;

import java.text.SimpleDateFormat;
import android.util.DisplayMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbEndpoint;

import android.os.storage.StorageVolume;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import android.os.SystemProperties;
import android.provider.Settings;

import java.util.Locale;
import android.content.res.Configuration;

import android.os.SystemProperties;
import android.net.ConnectivityManager;
import android.net.EthernetManager;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	String TAG="ProductTest", mLocalRecordFile="", nowPlayFile="";
	//EditText editText;
	//private CheckBox cbWifi, cbBT, cbPlayback, cbHDMIRx, cbHDMIRxRecord, cbReadWrite, cbPeripheral, cbRemoteCtrl;
	//private Button btnTest, btnComplete, btnExit, btnUnSel;
	private TextView txtUSB1, txtUSB2, txtUSB3, txtTypeC, txtSDCard, txtSATA1, txtSATA2, txtBT, txtEthernetIP, txtEthernetMAC, txtEthernetHint, txtWifiIP, txtWifiMAC, txtWifiHint, txtDDR, txtFlash, txtVersion, txtPlayState;
	private WifiManager mWifiManager=null;
	private List<ScanResult> mScanResults;
	private BluetoothAdapter bluetoothAdapter=null;
	private BluetoothSocket btSocket;
	private ArrayList<String> deviceList, mediaList;
	private int mediaIdx=0, maxVolume;
	private MediaPlayer mPlayer=null;
	private SurfaceHolder tx_holder;
	private SurfaceView tx_view;
	private Camera mCamera=null;
	private AudioManager audioManager;
	private Timer btTimer=null, wifiTimer=null, playbackTimer=null, rxRecordTimer=null, rxTimer=null, peripheralTimer=null;
	private Boolean cntUSB1=false, cntUSB2=false, cntUSB3=false, cntSATA1=false, cntSATA2=false, cntSD=false, cntTypeC=false, autoTest=true, btTest=false, wifiTest=false;; 
	private MediaRecorder mMediaRecorder=null; 
	private static final String FILENAME_PROC_VERSION = "/proc/version";
	SimpleDateFormat formatter=null;
	private int cntBlueDevices=0;

	String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	int UNINSTALL_REQUEST_CODE=1;

    private static StorageManager mStorageManager = null;
    private Context mContext;
    private static String[] mExternalPaths = null;
    private FileWriter logFile=null;

    private Locale locale = Locale.SIMPLIFIED_CHINESE;
    private String ACTION_USB_PERMISSION = "tw.g35gtwcms.android.test.list_usb_otg.USB_PERMISSION";
    private String mVersion = "106-01-18-001", logFileName="", media_provider_state="", media_scanner_state="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this.getApplicationContext();

		formatter = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");

        media_provider_state = SystemProperties.get("persist.media.RTKMediaProvider", "false");
        media_scanner_state = SystemProperties.get("rtk.mediascanner.forcestopscan", "false");
		SystemProperties.set("persist.media.RTKMediaProvider", "false");
		SystemProperties.set("rtk.mediascanner.forcestopscan", "true");
		
		txtUSB1 = (TextView)findViewById(R.id.usb1);
		txtUSB2 = (TextView)findViewById(R.id.usb2);
		txtUSB3 = (TextView)findViewById(R.id.usb3);
		//txtTypeC = (TextView)findViewById(R.id.type_c);
		txtSDCard = (TextView)findViewById(R.id.sdcard); 
		txtSATA1 = (TextView)findViewById(R.id.sata1);
		txtSATA2 = (TextView)findViewById(R.id.sata2);
		txtBT = (TextView)findViewById(R.id.bt);
		txtEthernetIP = (TextView)findViewById(R.id.ethernet_ip);
		txtEthernetMAC = (TextView)findViewById(R.id.ethernet_mac);
		txtEthernetHint = (TextView)findViewById(R.id.ethernet_hint);
		txtWifiHint = (TextView)findViewById(R.id.wifi_hint);
		txtWifiIP = (TextView)findViewById(R.id.wifi_ip);
		txtWifiMAC = (TextView)findViewById(R.id.wifi_mac);
		txtDDR = (TextView)findViewById(R.id.ddr);
		txtFlash = (TextView)findViewById(R.id.flash);
		txtVersion = (TextView)findViewById(R.id.version);
		txtPlayState = (TextView)findViewById(R.id.play_state);

		setTitle(getResources().getString(R.string.app_name)+"  (version: "+mVersion+")");

		openLogFile();
        logMsg("\r\n\r\n######################  start Product Test ##############################"); 

        base_test();
        versionInfo();
		showStorageInfo();
		txtVersion.setText(Build.DISPLAY);

		tx_view = (SurfaceView)findViewById(R.id.tx_preview);
		tx_holder = tx_view.getHolder();
		tx_holder.addCallback(this);  // 設置回調函數
        tx_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, maxVolume);
        
        mLocalRecordFile = Environment.getExternalStorageDirectory().getPath()+"/rx_record.ts";    
        mediaList = new ArrayList<String>();             

        //registerHomeKeyReceiver();
        registerUsbBroadcast();
        registerEthernetBroadcast();
	}


	@Override
	protected void onDestroy() {
	        super.onDestroy();
	        
	        saveLogFile();
	        //finishBluetoothTest();
	        // unregisterReceiver(mHomeKeyReceiver);
	        unregisterReceiver(mUsbReceiver);
	        unregisterReceiver(mEthernetReceiver);
	        
	        SystemProperties.set("persist.media.RTKMediaProvider", media_provider_state);
	        SystemProperties.set("rtk.mediascanner.forcestopscan", media_scanner_state);
	}
	 
	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,   int height) {
        // TODO Auto-generated method stub
        /*
        if (!Locale.getDefault().equals(locale)){
	        Locale.setDefault(locale);//設置選定的語言
			Configuration config = new Configuration();
			config.locale = locale;
			this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
			this.finish();
				
			//重啟當前介面
			this.startActivity(this.getIntent());
		}
		*/

    	advenceTest();
    }
 
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	     // TODO Auto-generated method stub
	    
	}
	 
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	        // TODO Auto-generated method stub         
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "KeyCode: "+keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP: 	   autoTest = false;   TestPlayback();     break;
		case KeyEvent.KEYCODE_DPAD_DOWN:   autoTest = false;   TestHDMIRx();       break;
		case KeyEvent.KEYCODE_MENU:        TestBT();           break;
		case KeyEvent.KEYCODE_DPAD_LEFT:   TestWifi();         break;
		//case KeyEvent.KEYCODE_DPAD_CENTER: Toast.makeText(MainActivity.this, mLocalRecordFile, Toast.LENGTH_LONG).show(); break;
		case KeyEvent.KEYCODE_DPAD_CENTER: logMsg("test ....");           break;
		case KeyEvent.KEYCODE_BACK:        do_Exit();          break;
		case KeyEvent.KEYCODE_0:  do_uninstall();     break;
		case KeyEvent.KEYCODE_1: test_all();    break;
		case KeyEvent.KEYCODE_INFO:  enumUSBDevice();          break;
	    }
		/*
		if (cbRemoteCtrl.isChecked()) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:    showStateStr("Press [Back] Key ...", "#0000FF");     break;
			case KeyEvent.KEYCODE_MENU:  showStateStr("Press [Menu] Key ...", "#0000FF");    break;
			case KeyEvent.KEYCODE_HOME:  showStateStr("Press [Home] Key ...", "#0000FF");    break;
			case KeyEvent.KEYCODE_MEDIA_NEXT: showStateStr("Press [Next] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_MEDIA_PREVIOUS: showStateStr("Press [Previous] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_VOLUME_UP: showStateStr("Press [Volume Up] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_VOLUME_DOWN: showStateStr("Press [Volume Down] Key ...", "#0000FF");    break;         
	        case KeyEvent.KEYCODE_DPAD_UP:  showStateStr("Press [Up] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_DPAD_DOWN:  showStateStr("Press [Down] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_DPAD_LEFT:  showStateStr("Press [Left] Key ...", "#0000FF");    breakg;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:  showStateStr("Press [Right] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_ENTER:
	        case KeyEvent.KEYCODE_DPAD_CENTER:  showStateStr("Press [Center] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_MEDIA_REWIND:  showStateStr("Press [Rewind] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:  showStateStr("Press [Forward] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_MEDIA_STOP:  showStateStr("Press [Stop] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_SEARCH:  showStateStr("Press [Search] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:  showStateStr("Press [Play/Pause] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_INFO:  showStateStr("Press [Info] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_TV:  showStateStr("Press [TV] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_WINDOW:  showStateStr("Press [Window] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_TV_INPUT:  showStateStr("Press [TV Input] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_POWER:  showStateStr("Press [Power] Key ...", "#0000FF");    break;
	        case KeyEvent.KEYCODE_UNKNOWN:  
	        default:   showStateStr("Press [Unknow] ["+keyCode+"] Key ...", "#0000FF");    break;
	        }
			return true;
	    } else {
	    	return false;
	    }
	    */
	    return false;
	}

/*
	@Override
	public void onAttachedToWindow() {  
		//showStateStr("Press [Home] Key ...", "#0000FF");
	 	this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);     
		super.onAttachedToWindow();    
	}
*/
	public void do_uninstall() {
		//SystemProperties.set("rtk.product.test", "false");
	//	Settings.Global.putInt(mContext.getContentResolver(), "product_test", 1);
	   do_Exit();
	}

	public void do_Exit() {
	/*	Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);  
        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        mContext.startActivity(intent); */ 
		System.exit(0);
	}

	public void test_all() {
		logMsg("************** Test Again ****************"); 
		autoTest = true;
		//base_test();
		advenceTest();
	}

	public void base_test() {
		checkPeripheral();
        //if (peripheralTimer == null) 
        //	createTimer(peripheralTimer, OnPeripheralTimer, 3000, 3000);		
        OnPeripheralTimerHandler.postDelayed(OnPeripheralTimerRunnable, 2000);

		getEthernetInfo();

		DisplayMetrics monitorsize = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(monitorsize);
        logMsg("resolution: "+monitorsize.widthPixels + "x" + monitorsize.heightPixels);
	}

	private Handler OnPeripheralTimerHandler = new Handler();
	private Runnable OnPeripheralTimerRunnable = new Runnable() {
		public void run() {
			checkPeripheral();
	        //base_test();
	        //OnPeripheralTimerHandler.removeCallbacks(OnPeripheralTimerRunnable);
	        OnPeripheralTimerHandler.postDelayed(OnPeripheralTimerRunnable, 2000);
	        sendMessage(13);
		}
	};

	public void advenceTest() {
    	TestBT();         
		TestWifi(); 
		TestPlayback();
		testNetworkConnect();
		//getUSBPath();
		//enumUSBDevice();
    }

	public void testNetworkConnect() {
		ConnectivityManager CM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		if (CM == null) {
			logMsg("Network unactivity.");
		} else {
			NetworkInfo info = CM.getActiveNetworkInfo(); 
			if (info != null && info.isConnected()) {
				if (!info.isAvailable()) {
					logMsg("Network unavailable.");
				} else {
					logMsg("network connect type: "+info.getTypeName());
				    logMsg("network connect state:"+info.getState());
				    logMsg("network is available? "+info.isAvailable());
				    logMsg("network is connect? "+info.isConnected());
				    logMsg("network is connecting? "+info.isConnectedOrConnecting());
				    logMsg("network has prolem? "+info.isFailover());
				    logMsg("network is roaming? "+info.isRoaming());
				}
			} else {
				logMsg("Network not connected.");
			}
		}
	}

	private void setDeviceStat(TextView view, Boolean connect, String deviceName) {
		if (connect) {
			view.setText(getResources().getString(R.string.connect));
			view.setTextColor(android.graphics.Color.BLUE);
			//logMsg(deviceName + getResources().getString(R.string.connect)); 
		} else {       
			view.setText(getResources().getString(R.string.disconnect));
			view.setTextColor(android.graphics.Color.RED);
			//logMsg(deviceName + getResources().getString(R.string.disconnect)); 
		}
	}

	private void setTextViewFm(TextView view, String txt, int color) {
		view.setText(txt);
		view.setTextColor(color);
	}

	public static  Camera.Size getOptimalCameraPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	private void registerEthernetBroadcast() {
		IntentFilter filter = new IntentFilter();
        filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		registerReceiver(mEthernetReceiver, filter);
	}

	private void registerWifiBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.setPriority(2147483647);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

		registerReceiver(mWifiReceiver, filter);
	}

	private void registerBtBroadcast() {
		IntentFilter filter = new IntentFilter();	
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mBtReceiver, filter);
	}

	private void registerUsbBroadcast() {
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);	
        filter.addAction(Intent.ACTION_MEDIA_SHARED);//如果SDCard未安裝,並通過USB大容量存儲共享返回
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd對像是存在並具有讀/寫權限
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但沒有被安裝
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //表明對象正在磁盤檢查
		filter.addAction(Intent.ACTION_MEDIA_EJECT);  //物理的拔出 SDCARD
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addDataScheme("file"); // 必須要有此行，否則無法收到廣播   

        registerReceiver(mUsbReceiver, filter);
	}	

	private  void registerHomeKeyReceiver() {
		IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mHomeKeyReceiver, homeFilter);
	}

	private  void unregisterHomeKeyReceiver() {
		unregisterReceiver(mHomeKeyReceiver);
	}

	public void delete_file(String fileName) {
		File file = new File(fileName);
        file.delete();
	}


    public void uninstallSelf() {
    	Uri packageUri = Uri.parse("package:com.realtek.ProductTest");
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        uninstallIntent.putExtra(uninstallIntent.EXTRA_RETURN_RESULT, true);
        //startActivity(uninstallIntent);
        startActivityForResult(uninstallIntent, UNINSTALL_REQUEST_CODE);
        System.exit(0);
    }

	OnClickListener btnCompleteOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			uninstallSelf();
		}
	};

	OnClickListener btnExitOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			System.exit(0);
		}
	};

	public void checkPeripheral() {   
		Boolean hasUSB1=false, hasUSB2=false, hasUSB3=false, hasSD=false, hasSATA1=false, hasSATA2=false;
	
		//if(!(cntUSB1 && cntUSB2 && cntUSB3)) {
		if(true) {
			cntUSB1 = false;
			cntUSB2 = false;
			cntUSB3 = false;
			try {
				String command = "ls /sys/bus/usb/devices";
				Process process = Runtime.getRuntime().exec(command);
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				int idx = -1;
				while ((line = reader.readLine()) != null) {
					idx = line.indexOf("1-1");
					if (idx >= 0) cntUSB1 = true;

					idx = line.indexOf("5-1");
					if (idx >= 0) cntUSB2 = true;
					idx = line.indexOf("6-1");
					if (idx >= 0) cntUSB2 = true;	

					idx = line.indexOf("8-1");
					if (idx >= 0) cntUSB3 = true;	
				}
				reader.close();
				process.waitFor();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		//if (!cntSD) {
		if(true) {
			cntSD = false;
			StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
			List<VolumeInfo> volumeInfos = sm.getVolumes();
			for (VolumeInfo vol : volumeInfos) {
				if(vol.type==VolumeInfo.TYPE_PUBLIC
						&& (vol.state==VolumeInfo.STATE_MOUNTED || vol.state==VolumeInfo.STATE_MOUNTED_READ_ONLY)){
					if (vol.disk.sysPath.indexOf("98000000.sdmmc")!=-1)
						cntSD = true;					
						}
			}
		}

		//if(!(cntSATA1 && cntSATA2)) {
		if(true) {
			cntSATA1 = false;
			cntSATA2 = false;
			try {
				String command = "ls /sys/class/scsi_disk";
				Process process = Runtime.getRuntime().exec(command);
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				int idx = -1;
				while ((line = reader.readLine()) != null) {
					idx = line.indexOf("0:0:0:0");
					if (idx >= 0) cntSATA1 = true;

					idx = line.indexOf("1:0:0:0");
					if (idx >= 0) cntSATA2 = true;	
				}
				reader.close();
				process.waitFor();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		sendMessage(12);
    }

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}

	private void enumUSBDevice() {
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbInterface usbInterface;
        String returnValue;

        logMsg("=================== enumUSBDevice ===================================================");
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            String info = //device.toString() + "\n\r" + 
                       "DeviceID: " + device.getDeviceId() + "\n\r" +
                       "DeviceName: " + device.getDeviceName() + "\n\r" +
                       "DeviceClass: " + device.getDeviceClass() + " - " +
                        //+ translateDeviceClass(device.getDeviceClass()) + "\n\r" +
                       "DeviceSubClass: " + device.getDeviceSubclass() + "\n\r" +
                       "VendorID: " + device.getVendorId() + "\n\r" +
                       "ProductID: " + device.getProductId() + "\n\r" +
                       "ProductName: " + device.getProductName() + "\n\r" +
                       "SerialNumber: " + device.getSerialNumber() + "\n\r" +
                       "InterfaceCount: " + device.getInterfaceCount() + "\n\r";
            logMsg("Device: \r\n"+info);

            /*
            for (int i = 0; i < device.getInterfaceCount(); i++) {
            	returnValue = "";
                usbInterface = device.getInterface(i);
                returnValue += "\n  Interface " + i;
                returnValue += "\n\tInterface ID: " + usbInterface.getId();
                returnValue += "\n\tClass: " + usbInterface.getInterfaceClass();
                returnValue += "\n\tProtocol: " + usbInterface.getInterfaceProtocol();
                returnValue += "\n\tSubclass: " + usbInterface.getInterfaceSubclass();
                returnValue += "\n\tEndpoint count: " + usbInterface.getEndpointCount();

                for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                    returnValue += "\n\t  Endpoint " + j;
                    returnValue += "\n\t\tAddress: " + usbInterface.getEndpoint(j).getAddress();
                    returnValue += "\n\t\tAttributes: " + usbInterface.getEndpoint(j).getAttributes();
                    returnValue += "\n\t\tDirection: " + usbInterface.getEndpoint(j).getDirection();
                    returnValue += "\n\t\tNumber: " + usbInterface.getEndpoint(j).getEndpointNumber();
                    returnValue += "\n\t\tInterval: " + usbInterface.getEndpoint(j).getInterval();
                    returnValue += "\n\t\tType: " + usbInterface.getEndpoint(j).getType();
                    returnValue += "\n\t\tMax packet size: " + usbInterface.getEndpoint(j).getMaxPacketSize();
                }
                logMsg("\r\n\r\nInterface: \r\n"+returnValue);
            }
            */
        }
	}

	public void getUSBPath() {
	        String usb = null;
	        if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
	        StorageVolume[] volumes = mStorageManager.getVolumeList();

	        for (int i = 0; i < volumes.length; i++) {
	            //if (volumes[i].isRemovable() && volumes[i].allowMassStorage() && volumes[i].getDescription(mContext).contains("USB")) {
	                usb = volumes[i].getDescription(mContext);
	                usb += "\r\nPath: "+volumes[i].getPath();
	                usb += "\r\nDescribeContents: "+volumes[i].describeContents();
	                usb += "\r\nState: "+volumes[i].getState();
	                usb += "\r\nUUID: "+volumes[i].getUuid();
	                usb += "\r\n";
	                //usb += "\r\n"+android.os.Environment.MEDIA_MOUNTED;
	                logMsg(usb);
	                Toast.makeText(mContext, usb, Toast.LENGTH_SHORT).show();
	            //}
	        }
	}

	private void usbConnection(UsbDevice device) {
        UsbEndpoint input, output;
        // find the right interface
        for(int i = 0; i < device.getInterfaceCount(); i++)  {
            // communications device class (CDC) type device
            if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                UsbInterface intf = device.getInterface(i);

                // find the endpoints
                for(int j = 0; j < intf.getEndpointCount(); j++)  {
                    if (intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT && intf.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        // from android to device
                        output = intf.getEndpoint(j);
                    }

                    if(intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN && intf.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        // from device to android
                        input = intf.getEndpoint(j);
                    }
                }
            }
        }
    }

	private int getHDMIRxState(){
        try {
        	logMsg("check HDMI Rx state.."); 
            BufferedReader br = new BufferedReader(new FileReader("/sys/devices/virtual/switch/rx_video/state"));
            String line = br.readLine().trim();
            br.close();
            return Integer.parseInt(line);
        } catch (IOException e) {
            logMsg("IOException /sys/devices/virtual/switch/rx_video/state"); 
            return -1;
        } catch (NumberFormatException e) {
            logMsg("NumberFormatException /sys/devices/virtual/switch/rx_video/state");
            return -1;
        }
    }

	public void TestHDMIRx() {
	         try {       
	         	  logMsg("test HDMI Rx ...");
	         	  //txtPlayState.setText(getResources().getString(R.string.rx_playback)); 
	         	  sendMessage(9);
                  if (getHDMIRxState() <=0){
                      Toast.makeText(mContext, "no HDMIRx connected", Toast.LENGTH_SHORT).show();
                      sendMessage(11);
                  } else {                  
                      createCamera();
                      if (mCamera != null) {
                          logMsg("start HDMI Rx Preview ..");
                          mCamera.startPreview();
                          //if (autoTest) 
                          //createTimer(rxTimer, OnRxTimer, 10000, -1);
                          OnRxTimerHandler.postDelayed(OnRxTimerRunnable, 10000);
                      }
                  }
             } catch (Exception e) {
		          logMsg("Test HDMI Rx fail");
		          logMsg(e.getMessage());
		          sendMessage(10);
	         }
	}
/*
	private TimerTask OnRxTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	logMsg("stop rx play ................................................. ");
	        	rxTimer = null;
                releaseCamera();    
                sendMessage(4);
                //if (autoTest) {
                	sendMessage(8);
                //}
	        }
	};	
*/
	private Handler OnRxTimerHandler = new Handler();
	private Runnable OnRxTimerRunnable = new Runnable() {
		public void run() {
			//update();
			logMsg("stop rx play ................................................. ");
			releaseCamera();   
			sendMessage(4);
			OnRxTimerHandler.removeCallbacks(OnRxTimerRunnable);
			sendMessage(8);
		}
	};

	public void TestHDMIRxRecord() {
		if (getHDMIRxState() <=0){
            Toast.makeText(mContext,"no HDMIRx connected",Toast.LENGTH_SHORT).show();
            logMsg("No HDMI Rx connected ....");
        } else {
            createCamera();
            try {
            	 logMsg("prepare media recorder ..");
                 mMediaRecorder.prepare();
                 mMediaRecorder.start();

		         createTimer(rxRecordTimer, OnRxRecordTimer, 10000, -1);
            } catch (IllegalStateException e) {
                 logMsg("IllegalStateException preparing MediaRecorder: " + e.getMessage());
                 releaseMediaRecorder();
            } catch (IOException e) {
                 logMsg("IOException preparing MediaRecorder: " + e.getMessage());
                 releaseMediaRecorder();
            }
        }
	}

	public void sendMessage(int flag) {
		Message message = new Message();
		message.what = flag;
		handler.sendMessage(message);
	}	

	private TimerTask OnPeripheralTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	checkPeripheral();
	        	//base_test();
	        	sendMessage(13);
	        }
	};	
	

	private TimerTask OnRxRecordTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	rxRecordTimer = null;
	        	logMsg("stop rx record ................................................. ");
	            mMediaRecorder.stop();  
                releaseMediaRecorder(); 
                releaseCamera();    
                sendMessage(3);
	        }
	};		

	public void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			logMsg("release MediaRecorder ................................................. ");
			mMediaRecorder.reset();
	        mMediaRecorder.release();
	        mMediaRecorder = null;
        }
	}

	private void releaseCamera() {
		if (mCamera != null) {
			logMsg("release Camera ................................................. ");
		    mCamera.stopPreview();
		    mCamera.release();
		    mCamera = null;
		}
	}

	private void createCamera() {
		if (mCamera == null) {
			try {
				logMsg("Open HDMI Rx ....");
				mCamera = Camera.open(1);
				Camera.Parameters parameters = mCamera.getParameters();
				List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
				Camera.Size optimalSize = getOptimalCameraPreviewSize(mSupportedPreviewSizes, 1920, 1080);
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.setPreviewSize(optimalSize.width, optimalSize.height);
				mCamera.setParameters(parameters);
				mCamera.setPreviewDisplay(tx_holder);

                int previewFrameRate = parameters.getPreviewFrameRate();
				int encodeBitRate = 5000000;
                int audioChannels = 2;
                int audioSampleRate = 48000;
                int audioBitRate = 64000;
                
                /*
                mMediaRecorder = new MediaRecorder();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.HDMIRX);
	            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.OUTPUT_FORMAT_MPEG2TS);
	            mMediaRecorder.setVideoSize(optimalSize.width, optimalSize.height);
	            mMediaRecorder.setVideoFrameRate(previewFrameRate);
	            mMediaRecorder.setVideoEncodingBitRate(encodeBitRate);
	            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
	            mMediaRecorder.setAudioChannels(audioChannels);
	            mMediaRecorder.setAudioSamplingRate(audioSampleRate);
	            mMediaRecorder.setAudioEncodingBitRate(audioBitRate);
	            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
	            mMediaRecorder.setOutputFile(mLocalRecordFile);
	            */
			} catch (Exception e) {
				logMsg("Open HDMI Rx fail");
				logMsg(e.getMessage());
				mCamera = null;
				sendMessage(10);
			}
		}
	}

	public void getEthernetInfo() {
		String networkIp = getResources().getString(R.string.disconnect), mac="-----";  
        try {  
           List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());  
           for (NetworkInterface iface : interfaces){  
               if (iface.getDisplayName().equals("eth0")) {  
                   List<InetAddress> addresses = Collections.list(iface.getInetAddresses());  
                   for (InetAddress address : addresses){  
                       if (address instanceof Inet4Address){  
                           networkIp = address.getHostAddress();  
                           break;
                       }  
                   }  
                   byte[] array = iface.getHardwareAddress();
                   StringBuilder stringBuilder = new StringBuilder("");
			       for (int i = 0; i < array.length; i++) {
			            int v = array[i] & 0xFF;
			            String hv = Integer.toHexString(v).toUpperCase();
			            if (hv.length() < 2) {
			                stringBuilder.append(0);
			            }
			            stringBuilder.append(hv).append("-");                   
			       }
			       mac = stringBuilder.substring(0, stringBuilder.length()- 1);
               }  
           }  

           txtEthernetIP.setText(networkIp); 
           txtEthernetMAC.setText(mac);
           txtEthernetHint.setText("");
           logMsg("Ethernet: "+networkIp+"    MAC:"+mac);
        } catch (SocketException e) {   
           txtEthernetMAC.setText("");
           txtEthernetHint.setText("");
           txtEthernetHint.setText("");
           logMsg("Ethernet: "+e.getMessage());
        }  
	}

    public void TestPeripheral() {
    	showStorageInfo();
    	versionInfo();
    }

    public void createTimer(Timer t_timer, TimerTask task, long msec, long per) {
		try {
		    if (t_timer != null) {
		    	t_timer.cancel(); 
		    	t_timer = null;
		    }	
		    t_timer = new Timer();
		    if (per >= 0) {
		    	t_timer.schedule(task, per, msec);
		    } else {	
		        t_timer.schedule(task, msec);
		    }
		} catch (Exception e) {
            logMsg(e.getMessage());
        }   
	}

    private BroadcastReceiver mHomeKeyReceiver = new BroadcastReceiver() {			
		private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
		private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
		private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
		    Log.i(TAG, "onReceive: action: " + action);
		    if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
		        // android.intent.action.CLOSE_SYSTEM_DIALOGS
		        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
		        Log.i(TAG, "reason: " + reason);

		        if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
		            // 短按Home鍵
		            Log.i(TAG, "homekey");
		        } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
		            // 長按Home鍵 或者 activity切換鍵
		            Log.i(TAG, "long press home key or activity switch");
		        } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
		            // 鎖屏
		            Log.i(TAG, "lock");
		        } else if  (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
		            // samsung 長按Home鍵
		            Log.i(TAG, "assist");
		        }
		    }
		}
	};

	private BroadcastReceiver mBootReceiver = new BroadcastReceiver() {
		@Override
	              public void onReceive(Context context,  Intent intent) {
                                     //if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                                          logMsg("Boot Completed ....");

                                          Intent activityIntent = new Intent(context, MainActivity.class);
                                          activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                          context.startActivity(activityIntent);
                                     //}
	              }
	};

	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
	              public void onReceive(Context context,  Intent intent) {
	              	  //intent.getAction());獲取存儲設備當前狀態
	              	  try {
                           logMsg("USB: "+intent.getAction()+"  ["+intent.getData().getPath()+"]");
                           //base_test();
                      } catch (Exception e) {
                           logMsg("Playback fail: "+e.getMessage());
                      }
                      /*
                      File file = new File(intent.getData().getPath()+"/test.mp4");		          
		              if (file.exists()) {
		                  PlayVideo(file.getPath());
		                  mediaList.clear();
		                  mediaIdx = 0;
		              }
		              */
	              }
	};


	public void TestPlayback() {
		String fileName="";
		File file=null;

        logMsg("Test Playback ..");
        if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);

        mediaList.clear();
		mediaIdx = 0;
        mExternalPaths = mStorageManager.getVolumePaths();
        for (String disk : mExternalPaths) {           	
        	fileName = disk+"/test.mp4";
        	file = new File(fileName);
        	if (file.exists()) {
		        mediaList.add(file.getPath());
		    }
        }

		if (mediaList.size() > 0)  {
			PlayVideo(mediaList.get(mediaIdx++));
		} else {
			logMsg("None playback file [test.mp4] in USB dongle .....");
		}

	}

	private void PlayVideo(String fileName) {
		try {
		      if (mCamera != null) releaseCamera();
		      if (mPlayer != null) { mPlayer.release();  mPlayer = null; } 
             
	          txtPlayState.setText(getResources().getString(R.string.tx_playback));    
	          logMsg("Test Playback ["+fileName+"]");

		      mPlayer = new MediaPlayer();
              mPlayer.setDataSource(fileName);
              mPlayer.setDisplay(tx_holder);
              mPlayer.prepare();
              mPlayer.setOnCompletionListener(mPlayerOnCompletionListener);
              mPlayer.start();

              //if (autoTest) createTimer(playbackTimer, OnPlaybackTimer, 10000, -1);
              OnPlaybackTimerHandler.postDelayed(OnPlaybackTimerRunnable, 10000);
        } catch (Exception e) {
              logMsg("Playback fail: "+e.getMessage());
        }
	}

/*
	private TimerTask OnPlaybackTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	logMsg("Playback end ...");
	        	playbackTimer = null;
   				releasePlayer();
   				sendMessage(4);
   				if (autoTest) TestHDMIRx();
	        }
	};
*/
    private Handler OnPlaybackTimerHandler = new Handler();
	private Runnable OnPlaybackTimerRunnable = new Runnable() {
		public void run() {
			logMsg("Playback end ...");
   			releasePlayer();
   			OnRxTimerHandler.removeCallbacks(OnPlaybackTimerRunnable);
   			sendMessage(4);
   			if (autoTest) TestHDMIRx();
		}
	};	

	MediaPlayer.OnCompletionListener mPlayerOnCompletionListener = new  MediaPlayer.OnCompletionListener() {
		@Override
        public void onCompletion(MediaPlayer player)  {           
                    txtPlayState.setText(getResources().getString(R.string.not_play));                       
                    if (mediaIdx < mediaList.size()) {
                    	nowPlayFile = mediaList.get(mediaIdx++);
                        PlayVideo(nowPlayFile);
                    } else {    
                        logMsg("playback test success......"); 
                        releasePlayer();
                        if (nowPlayFile.equals(mLocalRecordFile))  {
                        	delete_file(nowPlayFile);
                        }	
                        nowPlayFile = "";
                    }
        }
	};

	public void releasePlayer() {
		if (mPlayer != null) {
		    mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }    
        if (playbackTimer != null) {
        	playbackTimer.cancel();
            playbackTimer = null; 
        }
	}

	public void TestBT() {
        txtBT.setText(getResources().getString(R.string.testing));
        logMsg("BT Test: "+getResources().getString(R.string.testing));

        cntBlueDevices = 0;

		deviceList = new ArrayList<String>();
		registerBtBroadcast();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	

		if  (bluetoothAdapter == null) {
		     setTextViewFm(txtBT, getResources().getString(R.string.not_support), android.graphics.Color.BLUE);	
		     logMsg("BT: "+getResources().getString(R.string.not_support));	     
		     txtBT.setText(getResources().getString(R.string.not_support));
		     btTest = false;
		} else {
			 if (!bluetoothAdapter.isEnabled()) {
		         //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		         //startActivityForResult(enableBtIntent, 0);
		         bluetoothAdapter.enable();
		         txtBT.setText(getResources().getString(R.string.openning));
		     }    
		     //String mydeviceaddress = bluetoothAdapter.getAddress();
	         //String mydevicename = bluetoothAdapter.getName();
	         //int state = bluetoothAdapter.getState();
			 //createTimer(btTimer, OnBtCheckTimer, 24000, -1);
			 OnBtTimerHandler.postDelayed(OnBtTimerRunnable, 24000);
			 btTest = true;
	         bluetoothAdapter.startDiscovery();
	         logMsg("BT: "+getResources().getString(R.string.start_discovery));	  
	    }
	}

/*
	private TimerTask OnBtCheckTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	if (btTest) {
	        	    logMsg("BT: "+getResources().getString(R.string.check_timeout));
	        	    btTimer = null;
                	finishBluetoothTest();
                	sendMessage(1);
                }	
	        }
	};
*/	
	private Handler OnBtTimerHandler = new Handler();
	private Runnable OnBtTimerRunnable = new Runnable() {
		public void run() {
			logMsg("BT: "+getResources().getString(R.string.check_timeout));
           	finishBluetoothTest();
           	OnBtTimerHandler.removeCallbacks(OnBtTimerRunnable);
           	sendMessage(1);
		}
	};

	private void finishBluetoothTest() {
		try {
			btTest = false;
			unregisterReceiver(mBtReceiver);

			logMsg("finish Bluetooth Test ..");
			if (btTimer != null) {
				logMsg("cancel bt timer ..");
			    btTimer.cancel();
			    btTimer = null;
			}    

			if  (bluetoothAdapter != null) {
			     if (bluetoothAdapter.isDiscovering())   bluetoothAdapter.cancelDiscovery();
			     bluetoothAdapter.disable();
			     bluetoothAdapter = null;
			}

			if (deviceList != null) deviceList.clear();
			deviceList = null;
		} catch (Exception e) {
              logMsg("Playback fail: "+e.getMessage());
        }
	}

	private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
		@Override
	              public void onReceive(Context context,  Intent intent) {
	                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
	                    BluetoothDevice  device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            	    if (intent == null)    return;
	                    String action = intent.getAction();
	                    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	                         String stateStr = "???";
	                         switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothDevice.ERROR)) {
	                         case BluetoothAdapter.STATE_OFF:            stateStr = "off";          break;
	                         case BluetoothAdapter.STATE_TURNING_ON:     stateStr = "turning on";   break;
	                         case BluetoothAdapter.STATE_ON:             stateStr = "on";                  
	                                                                     bluetoothAdapter.startDiscovery();
	                                                                     break;
	                         case BluetoothAdapter.STATE_TURNING_OFF:    stateStr = "turning off";   break;
	                         }
	                         logMsg("Bluetooth status = " + stateStr);
	                    } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
	                        if (!deviceList.contains(device.getName())) {
	                        	 cntBlueDevices++;
	                             deviceList.add(device.getName());
	                             if (device.getBondState() == BluetoothDevice.BOND_NONE) {
	                                  logMsg("BluetoothDevice: "+device.getName()+"["+device.getAddress()+"]"+device.getType());
	                                  //if (device.getName() != null && device.getType() == 1) {
	                                  	//String pinCode="0000";
	                                  	//device.setPin(pinCode.getBytes());
                                                   //device.createBond();
	                                  //}
	                             } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
	                                  logMsg("BluetoothDevice: "+device.getName()+"|"+device.getAddress()+"]  (BONDED)"+device.getType());
	                                  //if (device.getType() == 1) {
	                                  //connectBluetoothDevice(device);
	                                  //}
	                             }   
	                        }
	                        if (cntBlueDevices > 0) {
                               logMsg("Bluetooth test success .... ");
	                       	   sendMessage(6);
	                       	   finishBluetoothTest();
	                        }
	                   }  else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
	                       logMsg("Bluetooth start scan ...................");
	                   } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
	                   	   finishBluetoothTest();
	                       logMsg("Bluetooth discovry finished .... ");
	                       if (cntBlueDevices == 0) {
	                       	   logMsg(getResources().getString(R.string.no_bt_device));
	                       	   sendMessage(5);
	                       	   //setTextViewFm(txtBT, getResources().getString(R.string.no_bt_device), android.graphics.Color.GFREEN);	
	                       } else {
	                       	   logMsg("Bluetooth test success .... ");
	                       	   sendMessage(6);
	                       	   //setTextViewFm(txtBT, getResources().getString(R.string.pass), android.graphics.Color.BLUE);	
	                       }
                           
	                       //connectBluetoothDevice(device);
	                   } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
	                       //device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                       switch (device.getBondState()) {
	                       case BluetoothDevice.BOND_BONDING: 
	                                 break;
	                       case BluetoothDevice.BOND_BONDED:
	                                 connectBluetoothDevice(device);// 連接設備
	                                 break;
	                       case BluetoothDevice.BOND_NONE:
	                                 //showStateStr("Bluetooth device ["+device.getName()+"] cancel bond ...................", "");
	                       default:
	                                 break;
	                       }
	                 }
	        }         
	};

	private void connectBluetoothDevice(BluetoothDevice btDev) {
                    UUID uuid = UUID.fromString(SPP_UUID);
                     try {
                     	     if (bluetoothAdapter.isDiscovering())  bluetoothAdapter.cancelDiscovery();
                     	     logMsg("Bluetooth:  createRfcommSocketToServiceRecord()  to "+btDev.getName()+" .....");

                             btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
                             logMsg("Bluetooth:  connect  to "+btDev.getName()+" .....");

                             btSocket.connect();
                             logMsg("Bluetooth test  success .... ");
                     } catch (Exception e) {
                            // TODO Auto-generated catch block
                     	     logMsg("Bluetooth:  connect  to "+btDev.getName()+" [fail]"+e.getMessage());
                             e.printStackTrace();
                     }
    }

	
	public void TestWifi() {
		registerWifiBroadcast();
		logMsg("Start test WIFI ..");	

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //createTimer(wifiTimer, OnWifiCheckTimer, 40000, -1);
        OnWifiTimerHandler.postDelayed(OnWifiTimerRunnable, 40000);
        wifiTest = true;
		
		setTextViewFm(txtWifiHint, getResources().getString(R.string.testing), android.graphics.Color.BLUE);
		
		mWifiManager.setWifiEnabled(true);
		mWifiManager.startScan();
	}

	private BroadcastReceiver mEthernetReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
	        if (action.equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)){
	            sendMessage(13);
	        }
        }      
	};


	private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				logMsg("Wi-fi scan resulte available action .....");
				mScanResults = mWifiManager.getScanResults();
				if (mScanResults.size() == 0) {
					logMsg("Wi-Fi Error");
				} else {
					ScanResult openAP=null;
					int cntAP=0;
					for (ScanResult result : mScanResults) {
						String SSID = result.SSID + "   (" + mWifiManager.calculateSignalLevel(result.level, 1001) + ") ";
						if (result.capabilities.contains("WPA")) {
							SSID += "[WPA]";
						} else if (result.capabilities.contains("WEP")) {
							SSID += "[WEP]";
						} else {
							SSID += "[OPEN]";
							openAP = result;
						}
						logMsg("find: "+SSID);
						cntAP++;
						//Toast.makeText(mContext, SSID, Toast.LENGTH_LONG).show();
						break;
					}
					if (cntAP > 0) {
						logMsg("Wifi test success...");
                        finishWifiTest();
                        sendMessage(7);
					}
					//connectOPEN(openAP.SSID);
				}
			} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_ENABLED:
					logMsg("Wi-Fi State enable");
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					logMsg("Wi-Fi state disable");
					break;
				}
			}
		}
	};

	private void connectWPA(String networkSSID, String networkPass) {
		logMsg("Wi-Fi connect "+networkSSID);

		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + networkSSID + "\"";
		wc.preSharedKey = "\"" + networkPass + "\"";
		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		int id = mWifiManager.addNetwork(wc);
		mWifiManager.disconnect();
		mWifiManager.enableNetwork(id, true);
		mWifiManager.reconnect();
	}

	private void connectWEP(String networkSSID, String networkPass) {
	}

	private void connectOPEN(String networkSSID) {
		logMsg("Wi-Fi connect "+networkSSID);

		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + networkSSID + "\"";
		wc.hiddenSSID = true;
		wc.priority = 0xBADBAD;
		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		int id = mWifiManager.addNetwork(wc);
		mWifiManager.disconnect();
		mWifiManager.enableNetwork(id, true);
		if (mWifiManager.reconnect()) {
		    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                                 int NETWORKID = wifiInfo.getNetworkId() ;
                                 int IPADRRESS = wifiInfo.getIpAddress() ;
                                 String IP = String.format("%d.%d.%d.%d", (IPADRRESS & 0xff), (IPADRRESS >> 8 & 0xff), (IPADRRESS >> 16 & 0xff),( IPADRRESS >> 24 & 0xff)) ;
		    logMsg("Wifi test success ...["+IP+"]["+wifiInfo.getMacAddress()+"]");
		} else {
		    logMsg("Wifi test fail ...");
		}
		finishWifiTest();
	}

/*
	private TimerTask OnWifiCheckTimer = new TimerTask() {
	        @Override
	        public void run() {
	        	wifiTimer = null;
	        	if (wifiTest) {
	        	    logMsg("Wifi: "+getResources().getString(R.string.check_timeout));	
		            sendMessage(2);
		            finishWifiTest();
		        }
	        }
	};
*/	
	private Handler OnWifiTimerHandler = new Handler();
	private Runnable OnWifiTimerRunnable = new Runnable() {
		public void run() {
			if (wifiTest) {
	            logMsg("Wifi: "+getResources().getString(R.string.check_timeout));	
	            OnWifiTimerHandler.removeCallbacks(OnWifiTimerRunnable);
		        sendMessage(2);
		        finishWifiTest();
		    }
		}
	};

	private void finishWifiTest() {
		logMsg("finishWifiTest .......");
		wifiTest = false;
		if (mWifiManager != null) {
		    mWifiManager.disconnect();
		    mWifiManager.setWifiEnabled(false);
		    mWifiManager = null;
		    unregisterReceiver(mWifiReceiver);
	    }
	}

	Handler handler = new Handler( ) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: setTextViewFm(txtBT, getResources().getString(R.string.check_timeout), android.graphics.Color.RED);  break;
			case 2: setTextViewFm(txtWifiHint, getResources().getString(R.string.check_timeout), android.graphics.Color.RED); break;
			case 3: PlayVideo(mLocalRecordFile);    break;
			case 4: setTextViewFm(txtPlayState, getResources().getString(R.string.not_play), android.graphics.Color.BLUE);  break; 
			case 5: setTextViewFm(txtBT, getResources().getString(R.string.no_bt_device), android.graphics.Color.GREEN);	break;
			case 6: setTextViewFm(txtBT, getResources().getString(R.string.pass), android.graphics.Color.BLUE);	 break;   
			case 7: setTextViewFm(txtWifiHint, getResources().getString(R.string.pass), android.graphics.Color.BLUE);	 break;     
			case 8: setTextViewFm(txtPlayState, getResources().getString(R.string.play_end), android.graphics.Color.BLUE); 	 
			        logMsg(getResources().getString(R.string.play_end));	
			        break;   
			case 9: txtPlayState.setText(getResources().getString(R.string.rx_playback));  break;
			case 10: setTextViewFm(txtPlayState, getResources().getString(R.string.rx_playback_fail), android.graphics.Color.RED); break;
			case 11: setTextViewFm(txtPlayState, getResources().getString(R.string.no_rx), android.graphics.Color.RED); break;
			case 12: setDeviceStat(txtUSB1, cntUSB1, "USB1: ");
			         setDeviceStat(txtUSB2, cntUSB2, "USB2: ");
					 setDeviceStat(txtUSB3, cntUSB3, "USB3: ");
					 //setDeviceStat(txtTypeC, cntTypeC, "USB Type-C: ");
					 setDeviceStat(txtSDCard, cntSD, "SDCard: ");
					 setDeviceStat(txtSATA1, cntSATA1, "SATA1: ");
					 setDeviceStat(txtSATA2, cntSATA2, "SATA2: ");
			         break;
			case 13: getEthernetInfo(); break;         
			}
			super.handleMessage(msg);
		}
	};


	public void showStorageInfo() {
		int privateCount = 0, publicCount=0;
        long privateUsedBytes = 0,  privateTotalBytes = 0, publicTotalBytes = 0;
        File path;

		try {
            if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);

            final List<VolumeInfo> volumes = mStorageManager.getVolumes();
            Collections.sort(volumes, VolumeInfo.getDescriptionComparator());

            for (VolumeInfo vol : volumes) {
            	 path = vol.getPath();
            	 //Toast.makeText(mContext, path.getPath(), Toast.LENGTH_LONG).show();
                 if (vol.getType() == VolumeInfo.TYPE_PRIVATE) {
                     if (vol.isMountedReadable()) {
                         privateUsedBytes += path.getTotalSpace() - path.getFreeSpace();
                         privateTotalBytes += path.getTotalSpace();
                     }
                 } else if (vol.getType() == VolumeInfo.TYPE_PUBLIC) {
                 	 //showStateStr("Storage: "+mStorageManager.getBestVolumeDescription(vol)+"  ["+Long.toString(path.getFreeSpace()/1048576)+" MB free "+Long.toString(path.getTotalSpace()/1048576)+" MB total]", "#0000FF");  
                 	 publicCount++;
                 	 publicTotalBytes += path.getTotalSpace();
                 }	
            }
			long myIntMem = privateTotalBytes/1073741824+3;
			String IntMem = Long.toString(myIntMem)+" GB";

			if (myIntMem <= 32 && myIntMem > 16)
				IntMem = Long.toString(32)+" GB";
			else if (myIntMem <=16 && myIntMem > 8)
				IntMem = Long.toString(16)+" GB";
			else if (myIntMem <=8 && myIntMem > 4)
				IntMem = Long.toString(8)+" GB";

            //String ExtMem = "USB x " +Integer.toString(publicCount);
            //showStateStr("Internal storage: "+IntMem, "#0000FF"); 
            txtFlash.setText(IntMem);
            
        } catch (Exception e) {
             e.printStackTrace();
             logMsg(e.getMessage());
        }      
	}

	public void versionInfo() {
		FileInputStream fis = null;
        String readStr;
        boolean isParse = false;
        double realUsedRam, realFreeRam, realTotalRam;

        MemInfoReader memReader = new MemInfoReader();
        memReader.readMemInfo();
        realTotalRam = memReader.getTotalSize();
        txtDDR.setText(Double.toString(Math.ceil(realTotalRam/1073741824))+" GB");

/*
        try {
            fis = new FileInputStream("/system/vendor/resource/rtk_version.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            StringBuilder sb = new StringBuilder();
            char[] inputBuf = new char[512];
            int len = 0;

            while((len = isr.read(inputBuf)) != -1) {
                sb.append(inputBuf, 0, len);
            }

            readStr = sb.toString();
            Log.d(TAG, "rtk_version: " + readStr);
            fis.close();

            String[] resArr = readStr.split("\n");
            // assign value 
            showStateStr("Android Version: "+resArr[1].split(" ")[1], "#0000FF");  
            showStateStr("DvdPlayer Version: "+resArr[2].split(" ")[1], "#0000FF");
            showStateStr("Firmware Version: "+resArr[3].split(" ")[1], "#0000FF");
            showStateStr("Bootcode Version: "+resArr[4].split(" ")[1], "#0000FF");
            showStateStr("Linux Kernel Version: "+resArr[5].split(" ")[1], "#0000FF");
            showStateStr("Android DT Version: "+resArr[6].split(" ")[1], "#0000FF");
            showStateStr("Rescue DT Version: "+resArr[7].split(" ")[1], "#0000FF");
            showStateStr("Android Rootfs Version: "+resArr[8].split(" ")[1], "#0000FF");
            showStateStr("Rescue Rootfs Version: "+resArr[9].split(" ")[1], "#0000FF");
            showStateStr("Mali Version: "+resArr[10].split(" ")[1], "#0000FF");
            showStateStr("NAS Version: "+resArr[11].split(" ")[1], "#0000FF");

            
        } catch (Exception e) {
            Log.d(TAG, "Open /system/vendor/resource/rtk_version.txt failed!");
        } finally {
            if (fis != null) {
                fis = null;
            }
        }
        */
	}
	private String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
        } catch (IOException e) {
            logMsg("IO Exception when getting kernel version for Device Info screen");
            return "Unavailable";
        }
    }

    public String formatKernelVersion(String rawKernelVersion) {
        String PROC_VERSION_REGEX =
            "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
            "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
            "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
            "(#\\d+) " +              /* group 3: "#1" */
            "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            logMsg("Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            logMsg("Regex match on /proc/version only returned " + m.groupCount() + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
            m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
            m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    public void openLogFile() {
    	try {
    		logFileName = "/tmp/product_test.log";
    		logFile = new FileWriter(logFileName, true);
    		Toast.makeText(mContext, "Open log file: "+logFileName, Toast.LENGTH_LONG).show();
    	} catch (Exception e) {
    		logFile = null;
            Toast.makeText(mContext, "Open log file: "+logFileName+" fail.", Toast.LENGTH_LONG).show();    
        }	
/*
    	File file=null;
	    if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
	    StorageVolume[] volumes = mStorageManager.getVolumeList();

	    for (int i = 0; i < volumes.length; i++) {  
	        //if (volumes[i].isRemovable() && volumes[i].allowMassStorage()) {
	        if (volumes[i].isRemovable()) {	
	        	logFileName = volumes[i].getPath()+"/product_test.log";
	        	try {
		        	file = new File(logFileName);
		        	if (file.exists()) break;
	        	} catch (Exception e) {
                    Toast.makeText(mContext, "Open log file: "+logFileName+" fail.", Toast.LENGTH_LONG).show();    
                }	
	        }
	    }
	    if (file != null) {
	    	try {
	    		logFile = new FileWriter(logFileName, true);
	    		Toast.makeText(mContext, "Open log file: "+logFileName, Toast.LENGTH_LONG).show();
	    	} catch (Exception e) {
	    		logFile = null;
                Toast.makeText(mContext, "Open log file: "+logFileName+" fail.", Toast.LENGTH_LONG).show();    
            }	
	    } else {
	    	logFile = null;
	    	Toast.makeText(mContext, "Open log file: "+logFileName+" fail.", Toast.LENGTH_LONG).show();
	    }
	    */
    }

    public void closeLogFile() {
    	try {
    		if (logFile != null) logFile.close();
        } catch (Exception e) {
        	Toast.makeText(mContext, "close log file error....", Toast.LENGTH_LONG).show();    
        }	
    }

    public void saveLogFile() {
    	String cmd;
        if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
	    StorageVolume[] volumes = mStorageManager.getVolumeList();

    	for (int i = 0; i < volumes.length; i++) {  
	        //if (volumes[i].isRemovable() && volumes[i].allowMassStorage()) {
	        if (volumes[i].isRemovable()) {	
	        	cmd = "cat "+logFileName+" >> "+volumes[i].getPath()+"/product_test.log";
	        	try {
		        	Process process = Runtime.getRuntime().exec(cmd);
                    process.waitFor();
	        	} catch (Exception e) {
                    Toast.makeText(mContext, "Open log file: "+logFileName+" fail.", Toast.LENGTH_LONG).show();    
                }	
                break;
	        }
	    }
    }

    public void logMsg(String msg) {
    	try {    	    
    	    String smsg = formatter.format(new Date())+"  "+msg+"\n\r";
    	    Log.d(TAG, smsg+"...");
    	    if (logFile != null) {
    	        logFile.write(smsg);
    	        logFile.flush();
    	    }
    	} catch (Exception e) {
    		logFile = null;
        	Toast.makeText(mContext, "write log file error....", Toast.LENGTH_LONG).show();    
        	Log.d(TAG, e.getMessage());
        }
    }

	public void ReadWriteFile() {
		long start_time=0, end_time=0, write_avg=0, read_avg=0, speed=0;
		int i=0, j=0, k=0, cnt=0;
		String fileName;
        
        if (mStorageManager == null) mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        mExternalPaths = mStorageManager.getVolumePaths();
        for (String disk : mExternalPaths) {           	
        	fileName = disk+"/write_test.txt";
        	for (j =0; j<10; j++) {
        	     start_time = System.currentTimeMillis();
        	     try {
        	     	 //FileOutputStream fos = new FileOutputStream(fileName, true);;
        	     	 //FileWriter fw = new FileWriter(fos.getFD());
        	         FileWriter fw = new FileWriter(fileName, true);
        	         for (i=0; i<1000; i++) {
        	              fw.write("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789\r\n");
                     }
                     fw.flush();
        	         fw.close();
        	         //fos.getFD().sync();
                     //fos.close();
        	     } catch(IOException e) {
        	     	 logMsg(e.getMessage());
                     e.printStackTrace();
                 }    

        	     end_time = System.currentTimeMillis();
        	     speed = (100000*1000000)/(end_time - start_time)/1048576;
        	     write_avg += speed;
        	     speed = 0;
            }

            read_avg = 0;
            for (j =0; j<10; j++) {
        	    start_time = System.currentTimeMillis();
	            try {
		            FileReader fr = new FileReader(fileName);
		            BufferedReader br = new BufferedReader(fr);
		            String temp = br.readLine(); //readLine()讀取一整行
		            while (temp != null) {
		                 temp = br.readLine();
		                k++; 
		            }
		            fr.close();
	            } catch(IOException e) {
	                e.printStackTrace();
	            }  
	            end_time = System.currentTimeMillis();
        	    speed = (100000*1000000)/(end_time - start_time)/1048576;
        	    read_avg += speed;
        	    speed = 0;
            }

            delete_file(fileName);
            
        	//Toast.makeText(mContext, "speed: "+Long.toString(10000*1000000/(end_time - start_time)/1024/1024)+ "M B/S", Toast.LENGTH_LONG).show();
        	logMsg(disk+"    write speed: "+Long.toString(write_avg/10)+ "M B/S    read: "+Long.toString(read_avg/10)+ "M B/S");  
            write_avg = 0;
        }
	}
}
