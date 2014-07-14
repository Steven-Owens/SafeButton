package com.example.safeButtton.ui;

import com.example.safeButtton.IScreamService;
import com.example.safeButtton.R;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class BLEDeviceController extends Activity {
	public final static String TAG = "iscream.BLEDeviceController";
	public static final String EXTRA_BLE_DEVICE= "BLE_DEVICE";
	
private TextView AlarmTestBox;
private IScreamService mBluetoothLeService;
private String mDeviceName;
private String mDeviceAddress;

//Code to manage Service lifecycle.
private final ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        mBluetoothLeService = ((IScreamService.LocalBinder) service).getService();
        if (!mBluetoothLeService.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        Log.d(TAG, "connecting BluetoothLeService to Device");
        // Automatically connects to the device upon successful start-up initialization.
        mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    	Log.d(TAG, "disconnected from BluetoothService");
        mBluetoothLeService = null;
    }
};

//Handles various events fired by the Service.
// ACTION_GATT_CONNECTED: connected to a GATT server.
// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
// ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
//                        or notification operations.
private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (IScreamService.ACTION_GATT_CONNECTED.equals(action)) {
            invalidateOptionsMenu();
        } else if (IScreamService.ACTION_GATT_DISCONNECTED.equals(action)) {
            invalidateOptionsMenu();
        } else if (IScreamService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        } else if (IScreamService.ACTION_DATA_AVAILABLE.equals(action)) {
            displayData(intent.getStringExtra(IScreamService.EXTRA_DATA));
        }
    }

	
};

private void displayData(String data) {
    if (data != null) {
    	//todo: if alarm
    	AlarmTestBox.setText("Alarm:" + data);
    }
}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_display);
		AlarmTestBox = (TextView) this.findViewById(R.id.testText);
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			BluetoothDevice device= (BluetoothDevice)bundle.getParcelable(EXTRA_BLE_DEVICE);
			mDeviceName = device.getName();
			mDeviceAddress = device.getAddress();
		}
		
		Intent ServiceIntent = IScreamService.makeBindIntent(this);
		Log.d(TAG, "binding to serivce");
		bindService(ServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}
	
	@Override
    protected void onDestroy() {
		unbindService(mServiceConnection);
		super.onDestroy();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IScreamService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(IScreamService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(IScreamService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(IScreamService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}