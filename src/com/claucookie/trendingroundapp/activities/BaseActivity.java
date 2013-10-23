package com.claucookie.trendingroundapp.activities;

import com.android.volley.Network;
import com.bugsense.trace.BugSenseHandler;
import com.claucookie.trendingroundapp.util.NetworkReceiver;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View.OnClickListener;

/**
 * Created by claucookie on 12/10/13.
 */
public class BaseActivity extends ActionBarActivity {

	private CharSequence mTitle;

	// The BroadcastReceiver that tracks network connectivity changes.
	private NetworkReceiver receiver = new NetworkReceiver();
	
	private BroadcastReceiver refreshReceiver;

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BugSenseHandler.startSession(this);

		// Registers BroadcastReceiver to track network connection changes.
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		this.registerReceiver(receiver, filter);
		
		

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Unregisters BroadcastReceiver when app is destroyed.
		if (receiver != null) {
			this.unregisterReceiver(receiver);
		}
		
		Crouton.cancelAllCroutons();

		BugSenseHandler.closeSession(this);
	}
	
	public Crouton createErrorMessage(String message, OnClickListener clickListener){
		
		Style style = null;
		// Wait until the crouton is clicked
		if( clickListener != null ){
			style = new Style.Builder().setConfiguration(
					new Configuration.Builder().setDuration(
							Configuration.DURATION_INFINITE).build()).build();
		}else{
			style = new Style.Builder().setConfiguration(
					new Configuration.Builder().setDuration(
							Configuration.DURATION_LONG).build()).build();
		}
		
		final Crouton crouton = Crouton.makeText( this, message, style);
		
		// Configure click listener
		if( clickListener != null ){
			
			crouton.setOnClickListener(clickListener);
		}
		
		return crouton;
	}
	
	

}
