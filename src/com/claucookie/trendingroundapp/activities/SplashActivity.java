package com.claucookie.trendingroundapp.activities;

import com.claucookie.trendingroundapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

public class SplashActivity extends FragmentActivity {

	private static final long SPLASH_TIME = 3000; // 3 seconds
	private Handler mHandler;
	private Runnable mJumpRunnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		// We set the runnable to be launched after splash time
		mJumpRunnable = new Runnable() {

			public void run() {
				jump();
			}
		};
		mHandler = new Handler();
		mHandler.postDelayed(mJumpRunnable, SPLASH_TIME);
	}

	private void jump() {
		// it is safe to use this code even if you
		// do not intend to allow users to skip the splash
		// It prevents two finish actions situation: time+touch event
		if (isFinishing())
			return;
		
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		jump();
		return true;
	}

}
