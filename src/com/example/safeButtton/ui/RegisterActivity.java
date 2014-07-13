package com.example.safeButtton.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.safeButtton.R;
import com.example.safeButtton.net.NetPost;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

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
				JSONObject newJSONObject = new JSONObject();
				try {
					newJSONObject.put("register", true);
					newJSONObject.put("user name", usernameText.getText().toString());
					newJSONObject.put("phone", phoneText.getText().toString());
					NetPost.postJSON(newJSONObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(new Intent(tempThis, MainActivity.class));
			}
		});
	}
}
