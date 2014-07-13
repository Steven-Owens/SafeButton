package com.example.safeButtton.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.safeButtton.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	//public static final LooperThread mWorkerThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		final EditText usernameText = (EditText) findViewById(R.id.YourNameText);
		final EditText phoneText = (EditText) findViewById(R.id.ICEPhoneText);
		
		Button RegisterButton = (Button) findViewById(R.id.RegisterButton);
		final RegisterActivity tempThis = this;
		RegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add Register stuff here
					//List<NameValuePair>
				startActivity(new Intent(tempThis, MainActivity.class));
			}
		});
	}
	
	
	private static class LooperThread extends Thread {
	      public Handler mHandler;
	      
	      public Looper myLooper;

	      public void run() {
	          Looper.prepare();

	          mHandler = new Handler() {
	              public void handleMessage(Message msg) {
	                  // process incoming messages here
	              }
	          };
	          myLooper = Looper.myLooper();
	          

	          Looper.loop();
	      }
	  }
}
