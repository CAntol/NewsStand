package edu.umd.umiacs.newsstand;

import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TopStoriesImageHandler extends DefaultHandler{
	private TopStoriesImageFeed _mFeed;
	private TopStoriesImageInfo _mInfos;

	private final int RSS_MEDIA_HTML = 1;
	private final int RSS_CAPTION = 2;
	private final int RSS_WIDTH = 3;
	private final int RSS_HEIGHT = 4;
	private final int RSS_REDIRECT = 5;
	private final int RSS_IS_DUPE = 6;


	int depth = 0;
	int currentstate = 0;
	/*
	 * Constructor
	 */
	TopStoriesImageHandler()
	{
	}

	/*
	 * getFeed - this returns our feed when all of the parsing is complete
	 */
	TopStoriesImageFeed getFeed()
	{
		return _mFeed;
	}


	@Override
	public void startDocument() throws SAXException
	{
		// initialize our TopStoriesFeed object - this will hold our parsed contents
		_mFeed = new TopStoriesImageFeed();
		// initialize the TopStoriesInfo object - we will use this as a crutch to grab the info from the channel
		// because the channel and items have very similar entries..
		_mInfos = new TopStoriesImageInfo();
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
			_mInfos = new TopStoriesImageInfo();
			return;
		}
		if (localName.equals("media_html"))
		{
			currentstate = RSS_MEDIA_HTML;
			return;
		}
		if (localName.equals("caption"))
		{
			currentstate = RSS_CAPTION;
			return;
		}
		if (localName.equals("width"))
		{
			currentstate = RSS_WIDTH;
			return;
		}
		if (localName.equals("height"))
		{
			currentstate = RSS_HEIGHT;
			return;
		}
		if (localName.equals("redirect"))
		{
			currentstate = RSS_REDIRECT;
			return;
		}
		if (localName.equals("isDupe"))
		{
			currentstate = RSS_IS_DUPE;
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
			_mFeed.addItem(_mInfos);
			return;
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
	{
		String theString = new String(ch, start, length);

		switch (currentstate)
		{
		case RSS_MEDIA_HTML:
			_mInfos.set_media_html(theString);
			currentstate = 0;
			break;
		case RSS_CAPTION:
			_mInfos.set_caption(theString);
			currentstate = 0;
			break;
		case RSS_WIDTH:
			_mInfos.set_width(theString);
			currentstate = 0;
			break;
		case RSS_HEIGHT:
			_mInfos.set_height(theString);
			currentstate = 0;
			break;
		case RSS_REDIRECT:
			_mInfos.set_redirect(theString);
			currentstate = 0;
			break;
		case RSS_IS_DUPE:
			_mInfos.set_is_dupe(theString);
			currentstate = 0;
			break;
		default:
			return;
		}
	}

	public class TopStoriesImageFeed {

		private int _mInfocount = 0;
		private List<TopStoriesImageInfo> _mInfolist;


		TopStoriesImageFeed()
		{
			_mInfolist = new Vector<TopStoriesImageInfo>(0); 
		}
		int addItem(TopStoriesImageInfo info)
		{
			_mInfolist.add(info);
			_mInfocount++;
			return _mInfocount;
		}
		TopStoriesImageInfo getNews(int location)
		{
			return _mInfolist.get(location);
		}
		List<TopStoriesImageInfo> getAllInfos()
		{
			return _mInfolist;
		}
		int getInfoCount()
		{
			return _mInfocount;
		}
	}

	public class TopStoriesImageInfo {

		private String _media_html = "";	// this is the image link
		private String _caption = "";
		private String _width = "";
		private String _height = "";
		private String _redirect = "";	//real news link
		private String _is_dupe = "0";

		TopStoriesImageInfo()
		{
		}

		public String get_media_html() {
			return _media_html;
		}

		public void set_media_html(String _media_html) {
			this._media_html = _media_html;
		}

		public String get_caption() {
			return _caption;
		}

		public void set_caption(String _caption) {
			this._caption = _caption;
		}

		public String get_width() {
			return _width;
		}

		public void set_width(String _width) {
			this._width = _width;
		}

		public String get_height() {
			return _height;
		}

		public void set_height(String _height) {
			this._height = _height;
		}

		public String get_redirect() {
			return _redirect;
		}

		public void set_redirect(String _redirect) {
			this._redirect = _redirect;
		}

		public String get_is_dupe() {
			return _is_dupe;
		}

		public void set_is_dupe(String _is_dupe) {
			this._is_dupe = _is_dupe;
		}


	}
}