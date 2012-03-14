package edu.umd.umiacs.newsstand;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class HTMLWebView extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		
		String html = getIntent().getStringExtra("html");
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
}
