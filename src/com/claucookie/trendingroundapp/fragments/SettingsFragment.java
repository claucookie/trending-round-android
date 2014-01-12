package com.claucookie.trendingroundapp.fragments;

import java.lang.reflect.InvocationTargetException;

import com.bugsense.trace.BugSenseHandler;
import com.claucookie.trendingroundapp.R;
import com.claucookie.trendingroundapp.activities.MainActivity;
import com.claucookie.trendingroundapp.util.IntentUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	
	/*
	 * Icons were obtained from
	 * http://www.iconarchive.com/show/cute-social-media-icons-by-designbolts/Twitter-icon.html
	 * 
	 */
	
	public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_settings, null);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((MainActivity) getActivity()).hideProgressBar();

		// Setting click to follow texts
		getActivity().findViewById(R.id.fs_facebook_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_instagram_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_twitter_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_blogger_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_contact_link_desc).setOnClickListener(sendContactEmailOnClickListener);

		getActivity().findViewById(R.id.fs_facebook_icon).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_instagram_icon).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_twitter_icon).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_blogger_icon).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_email_icon).setOnClickListener(sendContactEmailOnClickListener);

	}
	
	/**
	 * This listener lets you assign an action to multiple buttons.
	 * This action consist of opening a website by adding the url.
	 */
	OnClickListener showWebsiteOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			String url = "";
			if( v == getActivity().findViewById(R.id.fs_facebook_follow_desc)){
				
				url = getActivity().getString(R.string.facebook_url);
			}else if( v == getActivity().findViewById(R.id.fs_twitter_follow_desc)){
				
				url = getActivity().getString(R.string.twitter_url);
			}else if( v == getActivity().findViewById(R.id.fs_instagram_follow_desc)){
				
				url = getActivity().getString(R.string.instagram_url);
			}else if( v == getActivity().findViewById(R.id.fs_blogger_follow_desc)){
				
				url = getActivity().getString(R.string.blogger_url);
			}
			
			if( !url.equals("") ){
				
				IntentUtils.goToWebsite(getActivity(), url);
			}
			
		}
	};
	
	OnClickListener sendContactEmailOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			IntentUtils.sendContacEmail(getActivity(), getString(R.string.contact_email_sender), getString(R.string.contact_email_text));
			
		}
	};

	

}
