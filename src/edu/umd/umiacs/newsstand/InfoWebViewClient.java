package edu.umd.umiacs.newsstand;

import android.webkit.WebView;
import android.webkit.WebViewClient;


public class InfoWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
	
	
}
