package com.claucookie.trendingroundapp.activities;

import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Network;
import com.claucookie.trendingroundapp.R;
import com.claucookie.trendingroundapp.fragments.PostsListFragment;
import com.claucookie.trendingroundapp.fragments.SettingsFragment;
import com.claucookie.trendingroundapp.util.NetworkReceiver;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends BaseActivity {

	public static final String BACK_STACK_MAIN = "BACK_STACK_MAIN";

	private static final int REFRESH = 1;
	public static final String WORDPRESS_URL = "http://trendinground.wordpress.com/feed/";
	public static final String BLOGSPOT_URL = "http://trendinground.blogspot.com/feeds/posts/default";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private LinearLayout mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private ProgressBar mProgressBar;

	private CharSequence mDrawerTitle;
	private String[] mMenuTitles;
	private Fragment mContent;
	private boolean mContentClicked = false;
	private IntentFilter mNoNetworkIntentFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mProgressBar = (ProgressBar) findViewById(R.id.progressDialog);

		mDrawerTitle = getTitle();
		setTitle(mDrawerTitle);
		mMenuTitles = getResources().getStringArray(R.array.menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
		mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mMenuTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_navigation_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {

				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				if (mContent != null && mContentClicked) {

					showProgressBar();
					switchFragment(mContent);
					mContentClicked = false;
				} else {
					getSupportActionBar().setTitle(getTitle());
				}
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Load default fragment
		//mContent = PostsListFragment.newInstance(WORDPRESS_URL);
		selectItem(0);
		setTitle(getResources().getStringArray(R.array.menu)[0]);
		switchFragment(mContent);

		
		// We create a filter to listen this kind of actions
		mNoNetworkIntentFilter = new IntentFilter(NetworkReceiver.NO_NETWORK_ACTION);
		

	}
	
	BroadcastReceiver noNetworkReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			createErrorMessage(getString(R.string.no_internet_connection), null).show();
			hideProgressBar();
			
		}
	};
	
	
	
	@Override
	public void onResume(){
		super.onResume();
		
		this.registerReceiver(noNetworkReceiver, mNoNetworkIntentFilter);
		
	}
	
	@Override 
	public void onPause(){
		super.onPause();
		
		this.unregisterReceiver(noNetworkReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		//menu.findItem(R.id.action_update).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			selectItem(position);
		}
	}

	/**
	 * Select 1 list (menu) item
	 * 
	 * @param position
	 */
	private void selectItem(int position) {

		// update the main content by replacing fragments

		switch (position) {
		case 0:
			mContent = PostsListFragment.newInstance(BLOGSPOT_URL);
			setTitle(getResources().getStringArray(R.array.menu)[position]);
			break;

		case 1:
			mContent = new SettingsFragment();
			setTitle(getResources().getStringArray(R.array.menu)[position]);
			break;

		default:
			break;
		}

		mContentClicked = true;

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		// if (!mDrawerLayout.isDrawerOpen(R.id.drawer_layout))
		// switchFragment(mContent);
		mDrawerLayout.closeDrawer(mDrawer);
	}

	/**
	 * Switch content fragment methods
	 * 
	 * @param fragment
	 */
	public void switchFragment(Fragment fragment) {


		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}

	public void switchFragmentAndAddToStack(Fragment fragment) {

		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack(BACK_STACK_MAIN).commit();

	}

	/**
	 * ProgressDialog methods
	 */

	public void showProgressBar() {

		// Show progressbar
		if (mProgressBar == null)
			mProgressBar = (ProgressBar) findViewById(R.id.progressDialog);

		if (mProgressBar != null && !mProgressBar.isShown() && !isFinishing()) {

			mProgressBar.setVisibility(View.VISIBLE);
		}
	}

	public void hideProgressBar() {

		// Hiding progressbar
		if (mProgressBar != null && mProgressBar.isShown() && !isFinishing()) {

			mProgressBar.setVisibility(View.GONE);
		}

	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * It is called when any fragment calls "starforactivity..." and wait a
	 * result.
	 */
	@Override
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		if (requestCode == SettingsFragment.REQUEST_CODE_RESOLVE_ERR) {

			if (mContent instanceof SettingsFragment) {

				mContent.onActivityResult(requestCode, responseCode, intent);
			}
		}

		super.onActivityResult(requestCode, responseCode, intent);
	}

}
