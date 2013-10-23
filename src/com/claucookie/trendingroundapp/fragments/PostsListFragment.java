package com.claucookie.trendingroundapp.fragments;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.bugsense.trace.BugSenseHandler;
import com.claucookie.trendingroundapp.R;
import com.claucookie.trendingroundapp.activities.MainActivity;
import com.claucookie.trendingroundapp.adapters.PostsListAdapter;
import com.claucookie.trendingroundapp.model.Post;
import com.claucookie.trendingroundapp.sax.RssParserSAX;
import com.claucookie.trendingroundapp.util.NetworkReceiver;
import com.claucookie.trendingroundapp.util.NetworkUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PostsListFragment extends ListFragment implements
		OnConnectionFailedListener {

	public static final String TAG = "PostListFragment";

	public static final String EXTRA_URL = "EXTRA_URL";

	private PostsListAdapter mAdapter;
	public boolean isRefreshClicked = false;
	private String mUrlFeed;
	private LoadingPostsFromRSSTask mRssTask;
	private LoadingAllDBPostsTask mDbTask;
	private SavingPostsIntoDBTask mSavePostsTask;
	private IntentFilter mNetworkAvailableIntentFilter;

	public static final PostsListFragment newInstance(String urlFeed) {
		PostsListFragment f = new PostsListFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_URL, urlFeed);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Initialize adapter
		if (mAdapter == null)
			mAdapter = new PostsListAdapter(getActivity());

		// Setting adapter
		setListAdapter(mAdapter);

		return inflater.inflate(R.layout.fragment_posts_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Getting fragment arguments
		mUrlFeed = getArguments().getString(EXTRA_URL);

		// Initializating
		mRssTask = new LoadingPostsFromRSSTask();
		mDbTask = new LoadingAllDBPostsTask();
		mSavePostsTask = new SavingPostsIntoDBTask();
		mNetworkAvailableIntentFilter = new IntentFilter(
				NetworkReceiver.NETWORK_AVAILABLE_ACTION);

		// Load posts from DB
		// loadDataFromDB();
		loadDataFromRSS();

	}

	BroadcastReceiver networkAvailableReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			loadDataFromRSS();

		}
	};

	@Override
	public void onResume() {
		super.onResume();

		getActivity().registerReceiver(networkAvailableReceiver,
				mNetworkAvailableIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().unregisterReceiver(networkAvailableReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Canceling pending tasks.
		mDbTask.cancel(true);
		mRssTask.cancel(true);
	}

	public void onRefreshClicked() {

		if (!isRefreshClicked) {

			isRefreshClicked = true;
			// loadDataFromDB();
			loadDataFromRSS();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void loadDataFromDB() {

		// Loading data in background
		mDbTask = new LoadingAllDBPostsTask();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {

			mDbTask.execute();
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void loadDataFromRSS() {

		if (NetworkUtils.isNetWorkAvailable(getActivity())) {

			// Loading data in background
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

				mRssTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						mUrlFeed);
			} else {

				mRssTask.execute(mUrlFeed);
			}
		}else{
			
			((MainActivity) getActivity()).hideProgressBar();
		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;

		newContent = new PostDetailFragment();
		((PostDetailFragment) newContent).setmPost(mAdapter.getItem(position));

		if (newContent != null) {
			((MainActivity) getActivity())
					.switchFragmentAndAddToStack(newContent);
		}

	}

	/**
	 * Async task to retrieve posts from url
	 * 
	 * @author claucookie
	 * 
	 */
	public class LoadingPostsFromRSSTask extends
			AsyncTask<String, Void, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {

			((MainActivity) getActivity()).showProgressBar();
		}

		@Override
		protected ArrayList<Post> doInBackground(String... urls) {

			String feed = urls[0];
			ArrayList<Post> posts = new ArrayList<Post>();

			try {
				RssParserSAX saxparser = new RssParserSAX(feed);

				posts = (ArrayList<Post>) saxparser.parse();

			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}

			return posts;

		}

		@Override
		protected void onPostExecute(ArrayList<Post> posts) {

			try {

				if (!isRemoving() && !isDetached()) {

					if (posts.size() == 0) {

						// TODO no posts
					} else {

						// Adding Posts to adapter
						mAdapter.clear();
						for (Post post : posts) {
							mAdapter.add(post);
							mAdapter.notifyDataSetChanged();
						}

						// Saving new posts
						// mSavePostsTask.execute(posts);

					}

					((MainActivity) getActivity()).hideProgressBar();

					// Activating flag and cancelling thread
					isRefreshClicked = false;

					// Canceling thread
					// cancel(true);
				}

			} catch (Exception e) {
				BugSenseHandler.sendException(e);
			}
		}

	}

	/**
	 * Async task to retrieve post from DB
	 * 
	 * @author claucookie
	 * 
	 */
	private class LoadingAllDBPostsTask extends
			AsyncTask<Void, Void, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {

			((MainActivity) getActivity()).showProgressBar();
		}

		@Override
		protected ArrayList<Post> doInBackground(Void... arg0) {

			// Getting posts from db
			ArrayList<Post> posts = (ArrayList<Post>) Post.getAll();

			if (posts != null && posts.size() == 0) {

				// Fast transaction
				ActiveAndroid.beginTransaction();
				try {
					for (Post post : posts) {

						if (!Post.postExists(post.link)) {
							// Save post into ddbb
							post.save();
						}

					}
					ActiveAndroid.setTransactionSuccessful();
				} catch (Exception e) {
					Log.e("TAG", e.getMessage());
				} finally {
					ActiveAndroid.endTransaction();
				}

				posts = (ArrayList<Post>) Post.getAll();
			}

			return posts;
		}

		@Override
		protected void onPostExecute(ArrayList<Post> posts) {

			try {

				if (!isRemoving() && !isDetached()) {

					if (posts.size() == 0) {

						// TODO no posts
					} else {

						// Adding Posts to adapter
						mAdapter.clear();
						for (Post post : posts) {
							mAdapter.add(post);
							mAdapter.notifyDataSetChanged();
						}

					}

					// Get data from RSS
					if (NetworkUtils.isNetWorkAvailable(getActivity())) {
						loadDataFromRSS();
					} else {
						((MainActivity) getActivity()).hideProgressBar();
					}

					// Hiding progressbar
					((MainActivity) getActivity()).hideProgressBar();

					// Activating flag and cancelling thread
					isRefreshClicked = false;

					// Canceling thread
					// cancel(true);
				}

			} catch (Exception e) {
				BugSenseHandler.sendException(e);
			}

		}

	}

	/**
	 * Async task to Save posts into DB
	 * 
	 * @author claucookie
	 * 
	 */
	private class SavingPostsIntoDBTask extends
			AsyncTask<ArrayList<Post>, Void, Boolean> {

		@Override
		protected Boolean doInBackground(ArrayList<Post>... arg) {

			// Getting posts from db
			ArrayList<Post> posts = arg[0];
			boolean success = false;

			if (posts != null && posts.size() > 0) {

				// Fast transaction
				ActiveAndroid.beginTransaction();
				try {
					for (Post post : posts) {

						if (!Post.postExists(post.link)) {
							// Save post into ddbb
							post.save();
						}

					}
					ActiveAndroid.setTransactionSuccessful();
					success = true;
				} catch (Exception e) {
					Log.e("TAG", e.getMessage());
					success = false;
				} finally {
					ActiveAndroid.endTransaction();
				}

			}

			return success;

		}

		@Override
		protected void onPostExecute(Boolean successs) {

			try {

				if (!isRemoving() && !isDetached()) {

					if (!successs) {

						Log.e(TAG,
								"No se han podido guardar los posts en la bbdd !!!");
					}

				}

				// Cancelling thread
				cancel(true);

			} catch (Exception e) {
				BugSenseHandler.sendException(e);
			}

		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		Toast.makeText(getActivity(), "Connection failed !!!",
				Toast.LENGTH_LONG);

	}

}
