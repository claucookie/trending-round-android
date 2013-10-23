package com.claucookie.trendingroundapp.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.drm.DrmErrorEvent;
import android.drm.DrmManagerClient;
import android.drm.DrmManagerClient.OnErrorListener;
import android.text.Html;

import com.claucookie.trendingroundapp.model.Post;


public class RssHandler extends DefaultHandler {
    private List<Post> posts;
    private Post currentPost;
    private StringBuilder sbText;
    private boolean isPost;
    private boolean isMedia;
    private boolean contentIsHtml;
 
    public List<Post> getPosts(){
        return posts;
    }
 
    @Override
    public void characters(char[] ch, int start, int length)
                   throws SAXException {
 
        super.characters(ch, start, length);
 
        if (this.currentPost != null)
            sbText.append(ch, start, length);
    }
 
    @Override
    public void endElement(String uri, String localName, String name)
                   throws SAXException {
 
        super.endElement(uri, localName, name);
 
        if ( this.currentPost != null && isPost ) {
 
        	// Item title, link, content
            if (!isMedia && localName.equals("title")) {
                currentPost.title = sbText.toString().trim();
            } else if (!isMedia && localName.equals("link")) {
                currentPost.link = sbText.toString().trim();
            } else if( !isMedia && localName.equals("pubDate") ){
            	// Sat, 24 Aug 2013 12:29:23 +0000
            	
            } else if( !isMedia && localName.equals("published") ){
            	// 2013-10-05T11:15:00.000-07:00
            	
            } else if (!isMedia && contentIsHtml && localName.equals("content")) {
            	currentPost.content = sbText.toString().trim();
            	contentIsHtml = false;
            } else if (!isMedia && name.equals("content:encoded")) {
            	currentPost.content = sbText.toString().trim();
            } else if (!isMedia && localName.equals("item") || localName.equals("entry")) {
                posts.add(currentPost);
                isPost = false;
                currentPost = null;
            
            //  Item image
            } else if( isMedia && name.equals("media:content") ){
            	isMedia = false;
            }
 
            sbText.setLength(0);
        }
    }
 
    @Override
    public void startDocument() throws SAXException {
 
        super.startDocument();
 
        posts = new ArrayList<Post>();
        sbText = new StringBuilder();
        isPost = false;
        currentPost = null;
        isMedia = false;
        contentIsHtml = false;
    }
 
    @Override
    public void startElement(String uri, String localName,
                   String name, Attributes attributes) throws SAXException {
 
        super.startElement(uri, localName, name, attributes);
 
        if (localName.equals("item") || localName.equals("entry")) {
            currentPost = new Post();
            isPost = true;
        }else if( isPost && name.equals("content") && attributes.getValue("type").equals("html") ){
        	contentIsHtml = true;
        }else if( isPost && name.equals("media:content") ){
        	isMedia = true;
        	currentPost.image = attributes.getValue("url");
        }
    }

	
}
