package com.claucookie.trendingroundapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

/**
 * This class will use the framework ActiveAndroid, to make Sqlite operations
 * easier.
 * 
 * @author claucookie
 * 
 */

@Table(name = "Posts")
public class Post extends Model {
	
	public static final String TAG = "Post.java";

	public static final String TABLE_NAME = "Posts";
	public static final String LINK_COLUMN = "Link";
	public static final String TITLE_COLUMN = "Title";
	public static final String IMAGE_COLUMN = "Image";
	public static final String DESCRIPTION_COLUMN = "Description";
	public static final String HTMLCONTENT_COLUMN = "HtmlContent";
	public static final String CATEGORY_COLUMN = "Categories";
	public static final String READ_COLUMN = "Read";
	public static final String TIMESTAMP_COLUMN = "Timestamp";

	@Column(name = LINK_COLUMN)
	public String link;

	@Column(name = TITLE_COLUMN)
	public String title;

	@Column(name = IMAGE_COLUMN)
	public String image;

	@Column(name = HTMLCONTENT_COLUMN)
	public String htmlContent;
	
	@Column(name = DESCRIPTION_COLUMN)
	public String description;

	@Column(name = CATEGORY_COLUMN)
	public String categories; // Categories separated by " ".
	
	@Column(name = READ_COLUMN)
	public boolean isRead;
	
	@Column(name = TIMESTAMP_COLUMN)
	public String timestamp;
 
	public Post() {
		super();
	}

	public Post(String link, String title, String image, String description, String htmlContent,
			String categories, boolean isRead, String timestamp) {
		super();

		this.link = link;
		this.title = title;
		this.image = image;
		this.description = description;
		this.htmlContent = htmlContent;
		this.categories = categories;
		this.isRead = isRead;
		this.timestamp = timestamp;
	}

	public static Post getPostByLink(String link) {

		return new Select().from(Post.class)
				.where(LINK_COLUMN + " like ?", link).executeSingle();
	}

	public static List<Post> getAll() {

		return new Select().from(Post.class).execute();
	}

	public static boolean postExists(String link) {
		
		boolean exists = true;

		try {

			Post post = new Select().from(Post.class)
					.where(LINK_COLUMN + " like ?", link).executeSingle();

			if (post == null)
				exists = false;

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return exists;
	}

}
