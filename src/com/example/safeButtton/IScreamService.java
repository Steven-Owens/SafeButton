package com.example.safeButtton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class IScreamService extends IntentService {

	public final static String TAG = "iscream.IScreamService";

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_SAFE_DEVICE_DATA = UUID
			.fromString("21819AB0-C937-4188-B0DB-B9621E1696CD");
	public final static UUID UUID_SAFE_DEVICE_SERVICE = UUID
			.fromString("195AE58A-437A-489B-B0CD-B7C9C394BAE4");

	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";// todo:
																								// may
																								// need
																								// to
																								// change
																								// this

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				Log.i(TAG, "Attempting to start service discovery:"
				// Attempts to discover services after successful connection.
						+ mBluetoothGatt.discoverServices());
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {

				final BluetoothGattCharacteristic dataCharacteristic = mBluetoothGatt
						.getService(UUID_SAFE_DEVICE_SERVICE)
						.getCharacteristic(UUID_SAFE_DEVICE_DATA);
				final int charaProp = dataCharacteristic.getProperties();
				// read the current value
				new Thread(new Runnable() {

					@Override
					public void run() {
						Log.d(TAG, "polling started");
						while (!closing.get()) {
							mBluetoothGatt
									.readCharacteristic(dataCharacteristic);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Log.d(TAG, "polling ended");
					}
				}).start();

				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					Log.d(TAG, "NOTIFY avilable");
					// register for Notification
					mBluetoothGatt.setCharacteristicNotification(
							dataCharacteristic, true);
				} else {
					Log.e(TAG, "NOTIFY not avilable");
				}
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private boolean alarmed = false;

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		Log.d(TAG, "broadcast: " + action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		Log.d(TAG, "update:" + characteristic.getUuid().toString());
		if (UUID_SAFE_DEVICE_DATA.equals(characteristic.getUuid())) {
			// todo:make this more specific
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, stringBuilder.toString());
				Log.d(TAG, "data: " + stringBuilder.toString());
				if(stringBuilder.toString().contains("01")){
	            	//alarm
	            	if (!alarmed ){
	            		alarm();
	            	}
	            	alarmed = true;
	            } else if (stringBuilder.toString().contains("02")){
	            	//reset alarm
	            	 Log.d(BroadcastTAG, "resetting");
	            	alarmed = false;
	            	//todo: code to server.
	            }
			}
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n"
						+ stringBuilder.toString());
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public IScreamService getService() {
			return IScreamService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		// todo: move this code to handleIntent
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		
		//registerReceiver(mAlarmReceiver, makeGattUpdateIntentFilter());

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		//unregisterReceiver(mAlarmReceiver);
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		//unregisterReceiver(mAlarmReceiver);
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	private AtomicBoolean closing;

	public IScreamService() {
		super("IScreamServiceWorkerThread");
		Log.i(TAG, "creating IScreamService");
		// setting up the Service to restart if stopped
		setIntentRedelivery(true);
		closing = new AtomicBoolean(false);
	}

	public void onDestroy() {
		Log.i(TAG, "entering onDestroy");
		closing.set(true);
		disconnect();
		close();
		super.onDestroy();
	}

	public static Intent makeBindIntent(Context context) {
		Log.i(TAG, "making bind Intent");
		Intent newIntent = new Intent(context, IScreamService.class);
		return newIntent;
	}

	/*
	 * public static Intent makeStartedIntent(Context context, final String
	 * address) { Log.i(TAG, "making started Intent"); Intent newIntent = new
	 * Intent(context, IScreamService.class);
	 * newIntent.putExtra("deviceAddress", address); return newIntent; }
	 */

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}
	
	private IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IScreamService.ACTION_DATA_AVAILABLE);
        return intentFilter;
	}
	
	public final static String BroadcastTAG = TAG + ".mAlarmReceiver";
	public final static String url = "http://10.4.98.65/~srinibadri/iScream/safebutton.php";
	
	private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver(){
		
		boolean alarmed = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (IScreamService.ACTION_DATA_AVAILABLE.equals(action)) {
	            if(intent.getStringExtra(IScreamService.EXTRA_DATA).contains("01")){
	            	//alarm
	            	if (!alarmed){
	            		alarm();
	            	}
	            	alarmed = true;
	            } else if (intent.getStringExtra(IScreamService.EXTRA_DATA).contains("02")){
	            	//reset alarm
	            	 Log.d(BroadcastTAG, "resetting");
	            	alarmed = false;
	            	//todo: code to server.
	            }
	        }
		}
		
	};
	
	public static void alarm(){
		Log.d(BroadcastTAG, "alarmed");
		HttpClient httpclient = new DefaultHttpClient();
		Log.d(BroadcastTAG, "creating post");
        HttpPost httppost = new HttpPost(url);
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("alarm", "true"));
        nameValuePairs.add(new BasicNameValuePair("sender", "Isabel"));
        try {

            //nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(pLat)));
            //nameValuePairs.add(new BasicNameValuePair("longtitude", Double.toString(pLong)));

            Log.d(BroadcastTAG, "Encoding");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            Log.d(BroadcastTAG, "posting");
            HttpResponse response = httpclient.execute(httppost);
            Log.d(BroadcastTAG, "posted");
        } catch (ClientProtocolException e) {
        	Log.e(BroadcastTAG, e.getMessage());
        } catch (IOException e) {
        	Log.e(BroadcastTAG, e.getMessage());
        }
	}

}
