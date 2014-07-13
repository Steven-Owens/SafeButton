package com.example.safeButtton.ui;

import com.example.safeButtton.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactSelectListAdapter extends ArrayAdapter<ContactData> {
	
	private final LayoutInflater mInflator;

	
	public ContactSelectListAdapter(Context context, int resource, LayoutInflater inflator) {
		super(context, resource);
		this.mInflator= inflator;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
        // General ListView optimization code.
        if (convertView == null) {
        	convertView = mInflator.inflate(R.layout.contact_row, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.contactName);
            viewHolder.ICEContactCB = (CheckBox) convertView.findViewById(R.id.ICEContact);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ContactData curContactData = (ContactData)getItem(position);
        final String ContactName = curContactData.getName();
        if (ContactName != null && ContactName.length() > 0)
            viewHolder.name.setText(ContactName);
        else
            viewHolder.name.setText("Unknown Contact");
        //viewHolder.ICEContactCB.setChecked(checked);

        return convertView;
	}
	
	public class ViewHolder {
		public TextView name;
		public CheckBox ICEContactCB;
	}
}
