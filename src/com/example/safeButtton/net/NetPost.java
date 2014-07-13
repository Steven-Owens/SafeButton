package com.example.safeButtton.net;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class NetPost {
	private static final LooperThread mWorkerThread;
	
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
	
	static {
		mWorkerThread = new LooperThread();
		mWorkerThread.start();
	}
	
	
	
	public static void postJSON(JSONObject inObject){
		mWorkerThread.mHandler.post(new Runnable() {
			@Override
			public void run() {
				//todo:add code to post a JSONObject here
				//HttpClient newHttpClient = org.apache.http.client
			}
		});
	}
}
