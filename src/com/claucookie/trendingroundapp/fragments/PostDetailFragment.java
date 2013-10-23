package com.claucookie.trendingroundapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.claucookie.trendingroundapp.R;
import com.claucookie.trendingroundapp.model.Post;


public class PostDetailFragment extends Fragment {

	private Post mPost;

	public Post getmPost() {
		return mPost;
	}

	public void setmPost(Post mPost) {
		this.mPost = mPost;
	}

	public PostDetailFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_post_detail, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Post post = mPost;

		if (post != null) {

			if (post.title != null) {
				TextView title = (TextView) getView().findViewById(
						R.id.fpd_title);
				title.setText(post.title);
			}
			/*
			if (post.link != null) {
				TextView link = (TextView) getView().findViewById(
						R.id.fpd_link_tv);
				link.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
					}
				});
			}
			*/
			if (post.content != null) {
				WebView content = (WebView) getView().findViewById(
						R.id.fpd_content_webview);
				content.loadData(post.content, "text/html", null);
			}

		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt("mColorRes", mColorRes);
	}
	
	

}
