package com.example.safeButtton.ui;

import com.example.safeButtton.IScreamService;
import com.example.safeButtton.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	public final static String TAG = "iscream.MainActivity";
	
	public final static int REQUEST_ENABLE_BT = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreate");
		setContentView(R.layout.activity_main);
		
		Button connectBluetoothButton = (Button) findViewById(R.id.ConnectDeviceButton);
		final MainActivity tempThis = this;
		connectBluetoothButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
					//display error
				} else {
					if (!mBluetoothAdapter.isEnabled()) {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					} else {
						continueBlueToothSetup();
					}
				}
			}
		});
		
		Button RegisterButton = (Button) findViewById(R.id.cmdRegister);
		RegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add Register stuff here
				startActivity(new Intent(tempThis, RegisterActivity.class));
			}
		});
		
		Button LoginButton = (Button) findViewById(R.id.cmdlogIn);
		LoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add Register stuff here
				startActivity(new Intent(tempThis, LoginActivity.class));
			}
		});
		
		//debug code
		Button DebugButton = (Button) findViewById(R.id.debugButton);
		DebugButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add Register stuff here
				Log.d(TAG, "debug");
				new Thread(new Runnable() {

					@Override
					public void run() {
						Log.d(TAG, "thread run");
						IScreamService.alarm();
					}
				}).start();
			}
		});
		
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (requestCode == REQUEST_ENABLE_BT){
			if (resultCode == RESULT_OK){
				continueBlueToothSetup();
			}
		}
	}
	
	private void continueBlueToothSetup(){
		startActivity(new Intent(this, BLEScannerActivity.class));
	}

}
