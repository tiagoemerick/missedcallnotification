package com.viisi.droid.missedcallnotification.activity.missedcallslog;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.viisi.droid.missedcallnotification.R;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ToSendTabActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("tab1").setIndicator(getApplicationContext().getString(R.string.tab_tosend_label), res.getDrawable(R.drawable.ic_tab1)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, SentTabActivity.class);
		spec = tabHost.newTabSpec("tab2").setIndicator(getApplicationContext().getString(R.string.tab_sent_label), res.getDrawable(R.drawable.ic_tab1)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ErrorTabActivity.class);
		spec = tabHost.newTabSpec("tab3").setIndicator(getApplicationContext().getString(R.string.tab_error_label), res.getDrawable(R.drawable.ic_tab1)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

}