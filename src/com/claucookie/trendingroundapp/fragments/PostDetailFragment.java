package com.claucookie.trendingroundapp.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
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
	private SimpleDateFormat dateSourceFormat;
	private SimpleDateFormat dateResultFormat;

	public Post getmPost() {
		return mPost;
	}

	public void setmPost(Post mPost) {
		this.mPost = mPost;
	}

	public PostDetailFragment(){
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
		dateSourceFormat = new SimpleDateFormat(getActivity().getString(
				R.string.date_format));
		dateResultFormat = new SimpleDateFormat(getActivity().getString(
				R.string.date_format2));

		if (post != null) {

			if (post.title != null) {
				TextView title = (TextView) getView().findViewById(
						R.id.fpd_title);
				title.setText(post.title);
			}
			
			if (post.htmlContent != null) {
				
				post.htmlContent = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + post.htmlContent;
				// lets assume we have /assets/style.css file
				WebView content = (WebView) getView().findViewById(
						R.id.fpd_content_webview);
				content.loadDataWithBaseURL("file:///android_asset/", post.htmlContent, "text/html", "UTF-8", null);
			}
			
			TextView dateText = (TextView) getView().findViewById(R.id.fpd_date);
			try {
				if ( post.timestamp != null && !post.timestamp.equals("") ) {
					Date date = dateSourceFormat.parse(post.timestamp);
					dateText.setText(dateResultFormat.format(date));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt("mColorRes", mColorRes);
	}
	
	

}
