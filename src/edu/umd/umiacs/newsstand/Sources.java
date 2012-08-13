package edu.umd.umiacs.newsstand;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Sources extends Activity implements On{

		SharedPreferences prefs;
		SharedPreferences.Editor editor;
	
		private ArrayList<Source> feed_sources = new ArrayList<Source>();
		
	   
	@Override
	@SuppressWarnings("unchecked")
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sources);
	        ListView feedListView = (ListView)findViewById(R.id.feedListView);
	        if (feedListView == null) {
	        	System.out.println("FEED LIST NULL");
	        }
	        
	        Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	        	this.feed_sources = (ArrayList<Source>)extras.get("feedSources");
	        	feedListView.setAdapter(new SourceAdapter());
	        }

	        
	      //  prefs = getSharedPreferences("sources", 0);
	      //  editor = prefs.edit();
	    }

	   	private ArrayList<String> getFeedSourceNames () {
	   		ArrayList<String> feedNames = new ArrayList<String>();
	   		for (int i = 0; i < feed_sources.size(); i++) {
	   			feedNames.add(feed_sources.get(i).get_name());
	   		}
	   		return feedNames;
	   	}
	   
	    @Override
	    public void onResume() {
	        super.onResume();
	    }
	    
	    @Override
	    public void onPause() {
	        super.onPause();
	    }
	

	    
	public void onBackPressed() {
		super.onBackPressed();
		
		
		
		if (getIntent().getBooleanExtra("ts", false))
			Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show();
	}
	
	class SourceAdapter extends ArrayAdapter<String> {
		SourceAdapter() {
			super(Sources.this, R.layout.sources_row, R.id.source_label, getFeedSourceNames());
		}
	}

}
