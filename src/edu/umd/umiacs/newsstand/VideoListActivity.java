package edu.umd.umiacs.newsstand;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListActivity extends ListActivity{
	private VideoRowData[] _mVideoData;
	private VideoListAdapter sAdapter;
	private String _mClusterID;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_mClusterID = getIntent().getStringExtra("cluster_id");
		loadData();
		sAdapter = new VideoListAdapter(this, _mVideoData);

		this.setListAdapter(sAdapter);

		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(arg1.getContext(), NewsStandWebView.class);
				i = i.putExtra("url", _mVideoData[arg2].get_mUrl());
				arg1.getContext().startActivity(i);
			}
		});
		this.getListView().setBackgroundColor(Color.WHITE);


	}

	private VideoRowData[] loadData(){

		String imageURL = "http://newsstand.umiacs.umd.edu/news/xml_videos?cluster_id=";
		//sample: http://newsstand.umiacs.umd.edu/news/xml_images?cluster_id=17287060
		imageURL += _mClusterID;

		try {
			URL url = new URL(imageURL);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			TopStoriesVideoHandler videoHandler = new TopStoriesVideoHandler();
			xmlreader.setContentHandler(videoHandler);
			InputSource is = new InputSource(url.openStream());
			xmlreader.parse(is);
			_mVideoData =  videoHandler.getFeed().getAllInfos();

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"imageHandler parsing exception", Toast.LENGTH_SHORT)
					.show();
			return null;
		}

		return _mVideoData;
	}

	public class VideoListAdapter extends ArrayAdapter<VideoRowData>{

		private VideoRowData[] _mValues;
		private Context _ctx;

		public VideoListAdapter(VideoListActivity context, VideoRowData[] allInfos) {
			super(context, android.R.id.list, allInfos);
			_ctx = context;

			this._mValues = allInfos;
		}


		public int getCount() {
			return _mValues.length;
		}

		public VideoRowData getItem(int position) {
			return _mValues[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v=convertView;
			if(convertView==null){
				LayoutInflater inflater = (LayoutInflater) _ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.videolistviewrow, parent, false);
			}
			v.setBackgroundColor(Color.WHITE);
			TextView textView=(TextView)v.findViewById(R.id.videotitle);
			textView.setText(_mValues[position].get_mTitle());

			TextView duration = (TextView) v.findViewById(R.id.videoduration);
			duration.setText(_mValues[position].get_mLength());

			TextView time = (TextView) v.findViewById(R.id.videotime);
			time.setText(_mValues[position]._mPubDate);

			TextView domain = (TextView) v.findViewById(R.id.videodomain);
			domain.setText(_mValues[position].get_mDomain());

			ImageView imageView=(ImageView)v.findViewById(R.id.videoimage);
			Bitmap bitmap = loadImage(_mValues[position].get_mPreview());
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);

			return v;
		}

		private Bitmap loadImage(String urlString){
			Bitmap bitmap = null;    
			try {

				InputStream inputStream = null;
				URL url = new URL(urlString);
				URLConnection conn = url.openConnection();

				HttpURLConnection httpConn = (HttpURLConnection)conn;
				httpConn.setRequestMethod("GET");
				httpConn.connect();

				if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					inputStream = httpConn.getInputStream();
				}

				bitmap = BitmapFactory.decodeStream(inputStream, null, null);
				inputStream.close();
			} catch (Exception e1) {
				bitmap = null;
			}
			return bitmap;
		}


	}

	public class VideoRowData {
		private String _mUrl;
		private String _mTitle;
		private String _mName;
		private String _mDomain;
		private String _mPubDate;
		private String _mPreview;
		private String _mLength;


		public String get_mUrl() {
			return _mUrl;
		}
		public void set_mUrl(String _mUrl) {
			this._mUrl = _mUrl;
		}
		public String get_mTitle() {
			return _mTitle;
		}
		public void set_mTitle(String _mTitle) {
			this._mTitle = _mTitle;
		}
		public String get_mName() {
			return _mName;
		}
		public void set_mName(String _mName) {
			this._mName = _mName;
		}
		public String get_mDomain() {
			return _mDomain;
		}
		public void set_mDomain(String _mDomain) {
			this._mDomain = _mDomain;
		}
		public String get_mPubDate() {
			return _mPubDate;
		}
		public void set_mPubDate(String _mPubDate) {
			this._mPubDate = _mPubDate;
		}
		public String get_mPreview() {
			return _mPreview;
		}
		public void set_mPreview(String _mPreview) {
			this._mPreview = _mPreview;
		}
		public String get_mLength() {
			return _mLength;
		}
		public void set_mLength(String _mlength) {
			this._mLength = _mlength;
		}
	}

	public class TopStoriesVideoHandler extends DefaultHandler {
		private TopStoriesVideoFeed _mFeed;
		private VideoRowData _mInfos;

		private final int RSS_URL = 1;
		private final int RSS_TITLE = 2;
		private final int RSS_NAME = 3;
		private final int RSS_DOMAIN = 4;
		private final int RSS_PUBDATE = 5;
		private final int RSS_PREVIEW = 6;
		private final int RSS_LENGTH = 7;


		int depth = 0;
		int currentstate = 0;
		/*
		 * Constructor
		 */
		TopStoriesVideoHandler()
		{
		}

		/*
		 * getFeed - this returns our feed when all of the parsing is complete
		 */
		TopStoriesVideoFeed getFeed()
		{
			return _mFeed;
		}


		@Override
		public void startDocument() throws SAXException
		{
			// initialize our TopStoriesFeed object - this will hold our parsed contents
			_mFeed = new TopStoriesVideoFeed();
			// initialize the TopStoriesInfo object - we will use this as a crutch to grab the info from the channel
			// because the channel and items have very similar entries..
			_mInfos = new VideoRowData();
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
				_mInfos = new VideoRowData();
				return;
			}
			if (localName.equals("url"))
			{
				currentstate = RSS_URL;
				return;
			}
			if (localName.equals("title"))
			{
				currentstate = RSS_TITLE;
				return;
			}
			if (localName.equals("name"))
			{
				currentstate = RSS_NAME;
				return;
			}
			if (localName.equals("domain"))
			{
				currentstate = RSS_DOMAIN;
				return;
			}
			if (localName.equals("pub_date"))
			{
				currentstate = RSS_PUBDATE;
				return;
			}
			if (localName.equals("preview"))
			{
				currentstate = RSS_PREVIEW;
				return;
			}
			if (localName.equals("length"))
			{
				currentstate = RSS_LENGTH;
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
			case RSS_URL:
				_mInfos.set_mUrl(theString);
				currentstate = 0;
				break;
			case RSS_TITLE:
				_mInfos.set_mTitle(theString);
				currentstate = 0;
				break;
			case RSS_NAME:
				_mInfos.set_mName(theString);
				currentstate = 0;
				break;
			case RSS_DOMAIN:
				_mInfos.set_mDomain(theString);
				currentstate = 0;
				break;
			case RSS_PREVIEW:
				_mInfos.set_mPreview(theString);
				currentstate = 0;
				break;
			case RSS_LENGTH:
				_mInfos.set_mLength(theString);
				currentstate = 0;
				break;
			case RSS_PUBDATE:
				_mInfos.set_mPubDate(theString);
				currentstate = 0;
				break;
			default:
				return;
			}
		}

	}

	public class TopStoriesVideoFeed {

		private int _mInfocount = 0;
		private List<VideoRowData> _mInfolist;


		TopStoriesVideoFeed()
		{
			_mInfolist = new Vector<VideoRowData>(0); 
		}
		int addItem(VideoRowData info)
		{
			_mInfolist.add(info);
			_mInfocount++;
			return _mInfocount;
		}
		VideoRowData getNews(int location)
		{
			return _mInfolist.get(location);
		}
		VideoRowData[] getAllInfos()
		{
			VideoRowData[] result = new VideoRowData[_mInfocount];
			for (int i = 0 ; i < result.length; i++){
				result[i] = _mInfolist.get(i);
			}
			return result;
		}
		int getInfoCount()
		{
			return _mInfocount;
		}
	}
}
