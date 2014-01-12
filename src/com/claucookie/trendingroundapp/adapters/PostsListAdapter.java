package com.claucookie.trendingroundapp.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.claucookie.trendingroundapp.R;
import com.claucookie.trendingroundapp.lazylist.ImageLoader;
import com.claucookie.trendingroundapp.model.Post;

/**
 * Posts list Adapter
 */
public class PostsListAdapter extends ArrayAdapter<Post> {

	private ImageLoader mImageLoader;
	private SimpleDateFormat dateSourceFormat;
	private SimpleDateFormat dateResultFormat;

	public PostsListAdapter(Context context) {
		super(context, 0);

		dateSourceFormat = new SimpleDateFormat(getContext().getString(
				R.string.date_format));
		dateResultFormat = new SimpleDateFormat(getContext().getString(
				R.string.date_format2));
		mImageLoader = new ImageLoader(context, context.getString(
				R.string.app_name).trim(), R.drawable.trendinground_stub4);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Post post = getItem(position);
		
		// Using ViewHolder pattern
		MenuViewHolder menuViewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.fragment_posts_list_row, null);

			menuViewHolder = new MenuViewHolder();
			menuViewHolder.title = (TextView) convertView
					.findViewById(R.id.fpl_row_title);
			menuViewHolder.description = (TextView) convertView
					.findViewById(R.id.fpl_row_desc);
			menuViewHolder.date = (TextView) convertView
					.findViewById(R.id.fpl_row_date);

			convertView.setTag(menuViewHolder);
		} else {
			menuViewHolder = (MenuViewHolder) convertView.getTag();
		}

		menuViewHolder.title.setText(post.title);
		
		menuViewHolder.description.setText(post.description);

		try {
			if ( post.timestamp != null && !post.timestamp.equals("") ) {
				Date date = dateSourceFormat.parse(post.timestamp);
				menuViewHolder.date.setText(dateResultFormat.format(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	public void onDestroyView(){
		
	}

	/**
	 * View Holder Class
	 * 
	 */
	static class MenuViewHolder {

		private TextView title;
		private TextView description;
		private TextView date;
	}

}
