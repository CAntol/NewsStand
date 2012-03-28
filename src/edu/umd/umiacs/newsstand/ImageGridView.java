package edu.umd.umiacs.newsstand;

import java.io.InputStream;
import java.net.*;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import edu.umd.umiacs.newsstand.TopStoriesImageHandler.TopStoriesImageInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageGridView extends Activity{
	private GridView _mGridView;
	private String _mCluster_id;

	private boolean[] _mMask;
	private int _mMode;

	private ImageAdapter adapterAll;
	private ImageAdapter adapterMark;
	private ImageAdapter adapterUnique;
	
	private String[] _mAllUrl;
	private String[] _mUniqueUrl;
	
	private String[] _mAllWebUrl;
	private String[] _mUniqueWebUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagegridview);

		_mCluster_id = getIntent().getStringExtra("cluster_id");
		_mMode = 0 ;
		
		BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
		bmfOptions.inSampleSize = 8;

		_mGridView = (GridView) findViewById(R.id.imagegridview);
		adapterAll = new ImageAdapter(this); 
		adapterMark = new ImageAdapter(this);
		adapterUnique = new ImageAdapter(this);

		initImageThumb();

		_mGridView.setAdapter(adapterAll);

		_mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent t = new Intent(ImageGridView.this, ImageCoverFlowActivity.class);
				if (_mMode != 2)
				{
					t = t.putExtra("web", _mAllWebUrl);
					t = t.putExtra("url", _mAllUrl);
				}
				else
				{
					t = t.putExtra("web", _mUniqueWebUrl);
					t = t.putExtra("url", _mUniqueUrl);
				}
				ImageGridView.this.startActivity(t);
			}
		});

	}
	
	//deallocates images that may still be in memory when leaving the grid view
	public void onPause() {
		for (int c = 0; c < _mGridView.getCount(); c++) {
			ImageView v = (ImageView) _mGridView.getChildAt(c);
			if (v != null)
				if (v.getDrawable() != null)
					v.getDrawable().setCallback(null);
		}
		super.onPause();
	}


	public void initImageThumb(){
		String imageURL = "http://newsstand.umiacs.umd.edu/news/xml_images?cluster_id=";
		//Sample: http://newsstand.umiacs.umd.edu/news/xml_images?cluster_id=17287060
		imageURL += _mCluster_id;

		try {
			URL url = new URL(imageURL);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			TopStoriesImageHandler imageHandler = new TopStoriesImageHandler();
			xmlreader.setContentHandler(imageHandler);
			InputSource is = new InputSource(url.openStream());
			xmlreader.parse(is);
			List<TopStoriesImageInfo> list =  imageHandler.getFeed().getAllInfos();

			int[] tempUnique = new int [list.size()];
			_mMask = new boolean[list.size()];

			Bitmap[] all = new Bitmap [list.size()];
			_mAllUrl = new String [list.size()];
			_mAllWebUrl = new String [list.size()];

			int i = 0; int j = 0;
			for(TopStoriesImageInfo info: list){
				_mAllUrl[i] = info.get_media_html();
				_mAllWebUrl[i] = info.get_redirect();
				//TODO remove cap, adjust dynamically within memory cap
				//if (i < 45)
					all[i] = loadImage(_mAllUrl[i]);
					if (all[i] == null)
						break;
				_mMask[i] = (info.get_is_dupe().equals("0")) ? false : true;
				if(!_mMask[i])
					tempUnique[j++] = i;
				i++;
			}

			j--; i--;	//adjust size
			if (j < 1)
				j = 0;
			Bitmap[] unique = new Bitmap[j];
			_mUniqueUrl = new String[j];
			
			for(int k = 0; k < j ; k ++){
				unique[k] = all[tempUnique[k]];
				_mUniqueUrl[k] = _mAllUrl[tempUnique[k]];
				_mAllWebUrl[i] = _mAllWebUrl[tempUnique[k]];
			}
			
			adapterAll.setBitmap(all);
			adapterMark.setBitmap(all);
			adapterMark.setMask(true);
			adapterMark.setMask(_mMask);
			adapterUnique.setBitmap(unique);


		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					//"imageHandler parsing exception", Toast.LENGTH_SHORT).show();
					"Unable to retrieve images", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.imageAll:
			if(_mMode != 0)
				_mGridView.setAdapter(adapterAll);
			_mMode = 0;
			break;        
		case R.id.imageMark:
			if(_mMode != 1)
				_mGridView.setAdapter(adapterMark);
			_mMode = 1;
			break;
		case R.id.imageUnique:
			if(_mMode != 2)
				_mGridView.setAdapter(adapterUnique);
			_mMode = 2;
			break;
		default:
			Toast.makeText(getApplicationContext(),
					"Functionality not yet implemented now.", Toast.LENGTH_SHORT).show();
		}
		return true;
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

			// TODO - crash on image load
			try {
				bitmap = BitmapFactory.decodeStream(inputStream, null, null);
			} catch(OutOfMemoryError e) {
				Log.i("NewsStand", "Memory Exceeded!");
				bitmap = null;
			}
			inputStream.close();
		} catch (Exception e1) {
			if (bitmap != null)
				bitmap.recycle();
			bitmap = null;
		}
		return bitmap;
	}

	public class ImageAdapter extends BaseAdapter {
		private ImageGridView mContext;
		private boolean[] mMask;
		private boolean fMask;

		// references to our images
		private Bitmap[] mBitmap;


		public ImageAdapter(Context c) {
			mContext = (ImageGridView) c;
			mMask = null;
			fMask = false;
		}

		public void setBitmap(Bitmap[] newBitmap){
			mBitmap = newBitmap;
		}

		public void setMask(boolean mask)
		{
			fMask = mask;
			this.notifyDataSetChanged();
		}

		public void setMask(boolean[] mask)
		{
			mMask = mask;
		}

		public int getCount() {
			if (mBitmap == null)
				return 0;
			return mBitmap.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mBitmap == null)
				return null;

			ImageView imageView = null;
			if (convertView == null) {  // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(2, 2, 2, 2);

			} else {
				imageView = (ImageView) convertView;
			}

			if (mBitmap[position] != null)
			{
				imageView.setImageBitmap(mBitmap[position]);

				if (fMask && mMask[position])
					imageView.setAlpha(127);
				else
					imageView.setAlpha(255);
			}
			else
			{
				imageView = new ImageView(mContext);
			}


			return imageView;
		}


	}
}
