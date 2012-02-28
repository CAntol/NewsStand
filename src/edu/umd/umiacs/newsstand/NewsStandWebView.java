package edu.umd.umiacs.newsstand;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NewsStandWebView extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
		
		String url = getIntent().getStringExtra("url");
		
		webView.loadUrl(url);

		webView.setWebViewClient(new InfoWebViewClient());

	}
}