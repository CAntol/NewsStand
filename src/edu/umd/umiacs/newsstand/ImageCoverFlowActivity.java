package edu.umd.umiacs.newsstand;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageCoverFlowActivity extends Activity{
	Bitmap[] mBitmap;
	ImageCoverFlow ImageCoverFlow;
	String[] url;
	String[] webUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url =  getIntent().getStringArrayExtra("url");
		webUrl = getIntent().getStringArrayExtra("web");
		
		if (url != null){
			initBitmap();
		}
		
		BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
		bmfOptions.inSampleSize = 8;

		ImageCoverFlow  = new ImageCoverFlow(this);

		ImageAdapter coverImageAdapter =  new ImageAdapter(this);

		//coverImageAdapter.createReflectedImages();

		ImageCoverFlow.setAdapter(coverImageAdapter);

		ImageCoverFlow.setSpacing(-20);
		ImageCoverFlow.setSelection(4, true);
		ImageCoverFlow.setAnimationDuration(1000);


		setContentView(ImageCoverFlow);
	}
	
	public void onPause() {
		for (int c = 0; c < ImageCoverFlow.getCount(); c++) {
			ImageView v = (ImageView) ImageCoverFlow.getChildAt(c);
			if (v != null)
				if (v.getDrawable() != null)
					v.getDrawable().setCallback(null);
		}
		super.onPause();
	}
	
	//TODO remove hard cap, adjust dynamically with memory cap
	private void initBitmap(){
		mBitmap = new Bitmap[url.length];
		for(int i =0; i<url.length; i++)
		{
			mBitmap[i] = loadImage(url[i]);
			if (mBitmap[i] == null)
				break;
		}
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
			} catch (OutOfMemoryError e) {
				Log.i("NewsStand", "Memory Exceeded!");
				bitmap = null;
			}
			inputStream.close();
		} catch (Exception e1) {
			bitmap.recycle();
			bitmap = null;
		}
		return bitmap;
	}

	public class ImageAdapter extends BaseAdapter {

		int mGalleryItemBackground;
		private Context mContext;

		private ImageView[] mImages;

		public ImageAdapter(Context c) {
			mContext = c;
			mImages = new ImageView[url.length];
		}

		public boolean createReflectedImages() {
			//The gap we want between the reflection and the original image
			final int reflectionGap = 3;


			int index = 0;
			for (Bitmap originalImage : mBitmap) {
				int width = originalImage.getWidth() * 2;
				int height = originalImage.getHeight() * 2;


				//This will not scale but will flip on the Y axis
				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				//Create a Bitmap with the flip matrix applied to it.
				//We only want the bottom half of the image
				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);


				//Create a new bitmap with same width but taller to fit reflection
				Bitmap bitmapWithReflection = Bitmap.createBitmap(width 
						, (height + height/2), Config.ARGB_8888);

				//Create a new Canvas with the bitmap that's big enough for
				//the image plus gap plus reflection
				Canvas canvas = new Canvas(bitmapWithReflection);
				//Draw in the original image
				canvas.drawBitmap(originalImage, 0, 0, null);
				//Draw in the gap
				Paint deafaultPaint = new Paint();
				canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
				//Draw in the reflection
				canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

				//Create a shader that is a linear gradient that covers the reflection
				Paint paint = new Paint(); 
				LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
						bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, 
						TileMode.CLAMP); 
				//Set the paint to use this shader (linear gradient)
				paint.setShader(shader); 
				//Set the Transfer mode to be porter duff and destination in
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
				//Draw a rectangle using the paint with our linear gradient
				canvas.drawRect(0, height, width, 
						bitmapWithReflection.getHeight() + reflectionGap, paint); 

				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView.setLayoutParams(new ImageCoverFlow.LayoutParams(240, 360));
				imageView.setScaleType(ScaleType.MATRIX);
				mImages[index++] = imageView;

			}
			return true;
		}

		public int getCount() {
			return mBitmap.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			//Use this code if you want to load from resources
			ImageView i = new ImageView(mContext);
			i.setImageBitmap(mBitmap[position]);
			i.setLayoutParams(new ImageCoverFlow.LayoutParams(260, 260));
			i.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 

			//Make sure we set anti-aliasing otherwise we get jaggies
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			i.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent t = new Intent(mContext, NewsStandWebView.class);
					t = t.putExtra("url", webUrl[position]);
					mContext.startActivity(t);
				}

			});
			return i;

			//return mImages[position];
		}
		/** Returns the size (0.0f to 1.0f) of the views 
		 * depending on the 'offset' to the center. */ 
		public float getScale(boolean focused, int offset) { 
			/* Formula: 1 / (2 ^ offset) */ 
			return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
		} 

	}
}
