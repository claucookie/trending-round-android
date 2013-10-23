package com.claucookie.trendingroundapp.adapters;

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

	public PostsListAdapter(Context context) {
		super(context, 0);
		
		mImageLoader = new ImageLoader(context, context.getString(R.string.app_name).trim(), R.drawable.trendinground_stub4);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// Using ViewHolder pattern
		MenuViewHolder menuViewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.fragment_posts_list_row, null);

			menuViewHolder = new MenuViewHolder();
			menuViewHolder.icon = (ImageView) convertView
					.findViewById(R.id.fpl_row_image);
			menuViewHolder.title = (TextView) convertView
					.findViewById(R.id.fpl_row_title);
			menuViewHolder.description = (TextView) convertView
					.findViewById(R.id.fpl_row_desc);

			convertView.setTag(menuViewHolder);
		} else {
			menuViewHolder = (MenuViewHolder) convertView.getTag();
		}
		
		if( getItem(position).image != null && !getItem(position).image.equals("") ){
			
			mImageLoader.DisplayImage(getItem(position).image, menuViewHolder.icon);
		}
		menuViewHolder.title.setText(getItem(position).title);
		menuViewHolder.description.setText(Html.fromHtml(getItem(position).content.toString().trim()).toString().trim());
		
		return convertView;
	}
	
	/**
	 * View Holder Class
	 * 
	 */
	static class MenuViewHolder {

		private ImageView icon;
		private TextView title;
		private TextView description;
	}

}
