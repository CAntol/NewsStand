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

public class Sources extends Activity{

		SharedPreferences prefs;
		SharedPreferences.Editor editor;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sources);
	        ListView feedListView = (ListView)findViewById(R.id.feedListView);
	        if (feedListView == null) {
	        	System.out.println("FEED LIST NULL");
	        }
	        
	        Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	        	@SuppressWarnings("unchecked")
				ArrayList<Source> feedSources = (ArrayList<Source>)extras.get("feedSources");
	        	ArrayList<String> sourceNames = new ArrayList<String>();

	        	for (int i = 0; i < feedSources.size(); i++) {
	        		sourceNames.add(feedSources.get(i).get_name());
	        	}   	
	        	
	        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        			android.R.layout.simple_list_item_1, sourceNames);
	        	
	        	if (adapter != null) {
	        		feedListView.setAdapter(adapter);
	        	}	
	        }

	        
	      //  prefs = getSharedPreferences("sources", 0);
	      //  editor = prefs.edit();
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

}
