package edu.umd.umiacs.newsstand;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ClusterViewer extends ListActivity {
	private List<Message> _mMessages;
	private String _url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Let's display the progress in the activity title bar, like the
		// browser app does.
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.clusterview);
		
		
		String gaz_id = this.getIntent().getStringExtra("gaz_id");
		_url = "http://newsstand.umiacs.umd.edu/news/xml_top_locations?gaz_id=" + gaz_id;
        loadFeed();
        
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		try{
		v.setBackgroundColor(Color.RED);
		Message clicked = _mMessages.get(position);
		String html = clicked.getMarkup();
		if(html != null){
			
			Intent i = new Intent(this, HTMLWebView.class);
			i = i.putExtra("html", html);
			this.startActivity(i);
		}
		else
			System.out.println("html page is null");
		}catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	private void loadFeed(){
    	try{
	    	FeedParser parser = new FeedParser(_url);
	    	_mMessages = parser.parse();
	    	List<String> titles = new ArrayList<String>(_mMessages.size());
	    	for (Message msg : _mMessages){
	    		titles.add(msg.getTitle());
	    	}
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.clusterrow,titles);
	    	this.setListAdapter(adapter);
	    	
    	} catch (Throwable t){
    		Toast.makeText(this.getBaseContext(), "ClusterView loadFeed error, url : " + _url, Toast.LENGTH_SHORT);
    	}
    }
}
