package com.claucookie.trendingroundapp.util;

import com.claucookie.trendingroundapp.R;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NetworkUtils {

	public static final String TAG = "NetworkUtils";

	public static boolean isNetWorkAvailable(Context context) {

		boolean isAvailable = false;

		try {

			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {

				isAvailable = true;
			} else {

				isAvailable = false;
				/*
				// Showing alert message
				final Crouton crouton = Crouton.makeText((Activity) context,
						context.getString(R.string.no_internet_connection), Style.ALERT);
				
				crouton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Crouton.cancelAllCroutons();
					}
				});
				crouton.show();
				*/
			}

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return isAvailable;

	}

}
