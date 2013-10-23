package com.claucookie.trendingroundapp.fragments;

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

public class SettingsFragment extends Fragment implements ConnectionCallbacks,
		OnConnectionFailedListener {
	
	/*
	 * Icons were obtained from
	 * http://www.iconarchive.com/show/cute-social-media-icons-by-designbolts/Twitter-icon.html
	 * 
	 */
	
	public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private SignInButton mGooglePlusButton;
	private TextView mGooglePlusConnected_tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_settings, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((MainActivity) getActivity()).showProgressBar();

		mPlusClient = new PlusClient.Builder(getActivity(), this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity").build();

		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(getActivity());
		mConnectionProgressDialog.setMessage("Signing in...");

		// Listener to connect googleplus account
		mGooglePlusButton = (SignInButton) getView().findViewById(
				R.id.sign_in_button);
		mGooglePlusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onGooglePlusClick();

			}
		});

		// Other textfields and buttons
		mGooglePlusConnected_tv = (TextView) getView().findViewById(
				R.id.fs_googleplus_connected);
		mGooglePlusConnected_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onAccountNameClick();
			}
		});

		// Showing G+ button is account is not connected
		showGPlusConnectionButtons(mPlusClient.isConnected());
		
		// Setting click to follow texts
		getActivity().findViewById(R.id.fs_facebook_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_instagram_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_twitter_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_blogger_follow_desc).setOnClickListener(showWebsiteOnClickListener);
		getActivity().findViewById(R.id.fs_contact_link_desc).setOnClickListener(sendContactEmailOnClickListener);

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

	/**
	 * Clicked to connect an account
	 */
	public void onGooglePlusClick() {

		if (!mPlusClient.isConnected()) {
			
			if( mConnectionProgressDialog != null 
					&& !mConnectionProgressDialog.isShowing() )
				
				mConnectionProgressDialog.show();

			if (mConnectionResult == null) {
				mPlusClient.connect();
			} else {
				if (mConnectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {

					Toast.makeText(getActivity(),
							" Update GooglePlay services!!!.",
							Toast.LENGTH_LONG).show();
				} else {

					((MainActivity) getActivity()).showProgressBar();
					try {
						mConnectionResult.startResolutionForResult(
								getActivity(), REQUEST_CODE_RESOLVE_ERR);
					} catch (SendIntentException e) {
						// Try connecting again.
						mConnectionResult = null;
						mPlusClient.connect();
					}
				}
			}
		}

	}

	/**
	 * Clicked to disconnect an account
	 */
	public void onAccountNameClick() {

		if (mPlusClient.isConnected()) {

			// ( (MainActivity) getActivity()).showProgressBar();

			mPlusClient.clearDefaultAccount();
			// mPlusClient.disconnect();
			showGPlusConnectionButtons(false);
			// Reinitializate g+client
			mPlusClient = new PlusClient.Builder(getActivity(), this, this)
					.setVisibleActivities(
							"http://schemas.google.com/AddActivity",
							"http://schemas.google.com/BuyActivity").build();
		}
	}

	public void showGPlusConnectionButtons(boolean isConnected) {

		// Showing G+ button is account is not connected
		// else we show connected account
		if (isConnected) {

			String accountName = mPlusClient.getAccountName();
			mGooglePlusButton.setVisibility(View.GONE);
			mGooglePlusConnected_tv.setVisibility(View.VISIBLE);
			mGooglePlusConnected_tv.setText(accountName);

		} else {

			mGooglePlusButton.setVisibility(View.VISIBLE);
			mGooglePlusConnected_tv.setVisibility(View.GONE);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(getActivity(),
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}

				
			}
			mConnectionProgressDialog.dismiss();
		}

		// Save the intent so that we can start an activity when the user clicks
		// the sign-in button.
		mConnectionResult = result;

		((MainActivity) getActivity()).hideProgressBar();

	}

	@Override
	public void onConnected(Bundle connectionHint) {

		try {

			// Showing G+ button is account is not connected
			showGPlusConnectionButtons(true);
			((MainActivity) getActivity()).hideProgressBar();
			mConnectionProgressDialog.dismiss();

		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		}
	}

	@Override
	public void onDisconnected() {

		try {

			// Showing G+ button is account is not connected
			showGPlusConnectionButtons(false);
			((MainActivity) getActivity()).hideProgressBar();

		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == getActivity().RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

}
