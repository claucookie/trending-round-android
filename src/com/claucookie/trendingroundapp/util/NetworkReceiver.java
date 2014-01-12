package com.claucookie.trendingroundapp.util;

import com.claucookie.trendingroundapp.R;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.keyboardsurfer.mobile.app.android.widget.crouton.R.style;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;

public class NetworkReceiver extends BroadcastReceiver {

	public static final String NO_NETWORK_ACTION = "NO.NETWORK.ACTION";
	public static final String NETWORK_AVAILABLE_ACTION = "NETWORK.AVAILABLE.ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();

		// Check if network get changed
		if (networkInfo != null) {

			// Sending broadcast message to all activities
			Intent i2 = new Intent();
			i2.setAction(NETWORK_AVAILABLE_ACTION);
			context.sendBroadcast(i2);

		} else {

			// Sending broadcast message to all activities
			Intent i2 = new Intent();
			i2.setAction(NO_NETWORK_ACTION);
			context.sendBroadcast(i2);

		}
	}
}