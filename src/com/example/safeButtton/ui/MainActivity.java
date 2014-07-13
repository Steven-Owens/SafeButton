package com.example.safeButtton.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.iscream.R;

public class MainActivity extends Activity {
	
	public final static int REQUEST_ENABLE_BT = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
