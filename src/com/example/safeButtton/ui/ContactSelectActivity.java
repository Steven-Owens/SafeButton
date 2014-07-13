package com.example.safeButtton.ui;

import com.example.iscream.R;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

public class ContactSelectActivity extends ListActivity {
	
	private static final int LOADER_ID = 5;
	private Uri mContactUri;
	private SimpleCursorAdapter mCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);
		Uri fullUri = ContactsContract.AUTHORITY_URI;
		
	}
}
