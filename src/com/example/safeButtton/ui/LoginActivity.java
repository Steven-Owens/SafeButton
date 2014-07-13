package com.example.safeButtton.ui;

import com.example.safeButtton.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		
		Button RegisterButton = (Button) findViewById(R.id.logInCmd);
		final LoginActivity tempThis = this;
		RegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//todo:add login stuff here
				startActivity(new Intent(tempThis, MainActivity.class));
			}
		});
	}
}
