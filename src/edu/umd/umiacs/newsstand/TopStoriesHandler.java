package edu.umd.umiacs.newsstand;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TopStoriesHandler extends DefaultHandler {
	private TopStoriesFeed _mFeed;
	private TopStoriesInfo _mMarker;

	private final int RSS_TITLE = 1;
	private final int RSS_DESCRIPTION = 2;
	private final int RSS_DOMAIN = 3;
	private final int RSS_URL = 4;
	private final int RSS_TIME = 5;
	private final int RSS_TOPIC = 6;
	private final int RSS_CLUSTER_ID = 7;
	private final int RSS_NUM_DOCS = 8;
	private final int RSS_NUM_IMAGES = 9;
	private final int RSS_NUM_VIDEOS = 10;
	

	int depth = 0;
	int currentstate = 0;
	/*
	 * Constructor
	 */
	TopStoriesHandler()
	{
	}

	/*
	 * getFeed - this returns our feed when all of the parsing is complete
	 */
	TopStoriesFeed getFeed()
	{
		return _mFeed;
	}


	@Override
	public void startDocument() throws SAXException
	{
		// initialize our TopStoriesFeed object - this will hold our parsed contents
		_mFeed = new TopStoriesFeed();
		// initialize the TopStoriesInfo object - we will use this as a crutch to grab the info from the channel
		// because the channel and items have very similar entries..
		_mMarker = new TopStoriesInfo();
	}

	@Override
	public void endDocument() throws SAXException
	{
	}

	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException
	{
		depth++;
		if (localName.equals("channel"))
		{
			currentstate = 0;
			return;
		}
		if (localName.equals("item"))
		{
			// create a new item
			_mMarker = new TopStoriesInfo();
			return;
		}
		if (localName.equals("title"))
		{
			currentstate = RSS_TITLE;
			return;
		}
		if (localName.equals("description"))
		{
			currentstate = RSS_DESCRIPTION;
			return;
		}
		if (localName.equals("domain"))
		{
			currentstate = RSS_DOMAIN;
			return;
		}
		if (localName.equals("url"))
		{
			currentstate = RSS_URL;
			return;
		}
		if (localName.equals("time"))
		{
			currentstate = RSS_TIME;
			return;
		}
		if (localName.equals("topic"))
		{
			currentstate = RSS_TOPIC;
			return;
		}
		if (localName.equals("cluster_id"))
		{
			currentstate = RSS_CLUSTER_ID;
			return;
		}
		if (localName.equals("num_docs"))
		{
			currentstate = RSS_NUM_DOCS;
			return;
		}
		if (localName.equals("num_images"))
		{
			currentstate = RSS_NUM_IMAGES;
			return;
		}
		if (localName.equals("num_videos"))
		{
			currentstate = RSS_NUM_VIDEOS;
			return;
		}

		// if we don't explicitly handle the element, make sure we don't wind up erroneously
		// storing a newline or other bogus data into one of our existing elements
		currentstate = 0;
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		depth--;
		if (localName.equals("item"))
		{
			// add our item to the list!
			_mFeed.addItem(_mMarker);
			return;
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
	{
		String theString = new String(ch, start, length);

		switch (currentstate)
		{
		case RSS_TITLE:
			_mMarker.setTitle(theString);
			currentstate = 0;
			break;
		case RSS_DESCRIPTION:
			_mMarker.setDescription(theString);
			currentstate = 0;
			break;
		case RSS_DOMAIN:
			_mMarker.setDomain(theString);
			currentstate = 0;
			break;
		case RSS_URL:
			_mMarker.setURL(theString);
			currentstate = 0;
			break;
		case RSS_TIME:
			_mMarker.setTime(theString);
			currentstate = 0;
			break;
		case RSS_TOPIC:
			_mMarker.setTopic(theString);
			currentstate = 0;
			break;
		case RSS_CLUSTER_ID:
			_mMarker.setCluster_id(theString);
			currentstate = 0;
			break;
		case RSS_NUM_DOCS:
			_mMarker.setNum_docs(theString);
			currentstate = 0;
			break;
		case RSS_NUM_IMAGES:
			_mMarker.setNum_images(theString);
			currentstate = 0;
			break;
		case RSS_NUM_VIDEOS:
			_mMarker.setNum_videos(theString);
			currentstate = 0;
			break;
		default:
			return;
		}
	}
}
