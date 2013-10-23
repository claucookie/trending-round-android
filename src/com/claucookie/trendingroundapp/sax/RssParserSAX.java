package com.claucookie.trendingroundapp.sax;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
 
import java.net.URL;
import javax.xml.parsers.SAXParser;
import java.net.MalformedURLException;
import javax.xml.parsers.SAXParserFactory;

import com.claucookie.trendingroundapp.model.Post;
 
public class RssParserSAX
{
    private URL rssUrl;
 
    public RssParserSAX(String url)
    {
        try
        {
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
 
	public List<Post> parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
        try
        {
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getPosts();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
 
    private InputStream getInputStream()
    {
        try
        {
            return rssUrl.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}