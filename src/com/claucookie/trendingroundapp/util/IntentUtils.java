package com.claucookie.trendingroundapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils {

	public static void goToWebsite(Context context, String url) {

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

	public static void sendContacEmail(Context context, String sender, String text) {

		if( text == null ) text = "";
		
		if (!sender.trim().equals("")) {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", sender, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, text);
			context.startActivity(Intent.createChooser(emailIntent, ""));
		}
	}

}
