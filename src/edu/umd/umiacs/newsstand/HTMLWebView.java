package edu.umd.umiacs.newsstand;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HTMLWebView extends Activity{
	
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		
		String html = getIntent().getStringExtra("html");
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			Log.i("DEBUG", "Going Back");
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, keyEvent);
	}
	
	private class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		/*
		 * TODO handle specific url differently
		 * should use this to display custom view for images and videos
		 */
		public void onLoadResource(WebView view, String url) {
			if (url.equals("")) {
				
			} else {
				super.onLoadResource(view, url);
			}
		}
	}
}
