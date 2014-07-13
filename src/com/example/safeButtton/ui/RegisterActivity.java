package com.example.safeButtton.ui;

import com.example.iscream.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		Button RegisterButton = (Button) findViewById(R.id.RegisterButton);
		final RegisterActivity tempThis = this;
		RegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add Register stuff here
				startActivity(new Intent(tempThis, MainActivity.class));
			}
		});
	}
}
