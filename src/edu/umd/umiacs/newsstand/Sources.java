package edu.umd.umiacs.newsstand;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Sources extends Activity {
	
	private ArrayList<Source> feed_sources = new ArrayList<Source>();
	private static String PACKAGE_NAME = "edu.umd.umiacs.newsstand";

	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sources);
	        ListView feedListView = (ListView)findViewById(R.id.feedListView);
	        if (feedListView == null) {
	        	System.out.println("FEED LIST NULL");
	        } else {
		        Bundle extras = getIntent().getExtras();
		        if (extras != null) {
		        	this.feed_sources = (ArrayList<Source>)extras.get("feedSources");
		        	feedListView.setAdapter(new SourceAdapter(this, R.layout.sources_row, feed_sources));
		        	feedListView.setItemsCanFocus(true);
		        }
	        }
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
	
	    private Activity getActivity() {
	    	return this;
	    }

	    
	public void onBackPressed() {
		Intent data = new Intent();
	    data.putExtra("feedSources", feed_sources);
	    setResult(RESULT_OK, data); 
		
		super.onBackPressed();
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
	
	private static class ViewHolder {
	    ImageView source_flag;
	    TextView source_label;
	    TextView source_description;
	    CheckBox source_checkbox;
	}
	
	class SourceAdapter extends ArrayAdapter<Source> {
		private Activity activity;
		private int resource;
		private LayoutInflater inflater = null;
		
		public SourceAdapter(Activity _activity, int _resource, ArrayList<Source> _items) {
			super(_activity, _resource, _items);
			inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        resource = _resource;
	        activity = _activity;
	        Log.i("please", "pelase");
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    View view = convertView;
		    ViewHolder holder;
		    if (convertView == null) {
		    	view = inflater.inflate(R.layout.sources_row, null);
		    	holder = new ViewHolder();
		    	holder.source_flag = (ImageView)view.findViewById(R.id.source_flag);
		    	holder.source_label = (TextView)view.findViewById(R.id.source_label);
		    	holder.source_description = (TextView)view.findViewById(R.id.source_description);
		    	holder.source_checkbox = (CheckBox)view.findViewById(R.id.source_checkbox);
		    	view.setTag(holder);
		    } else {
		    	holder = (ViewHolder)view.getTag();
		    }
		    
		    Source current_source = feed_sources.get(position);
		    
		    holder.source_label.setText(current_source.get_name());
		    
			String source_loc = current_source.get_source_location();
			if (source_loc != null && !source_loc.equals("")) {
				holder.source_flag.setImageResource(getResources().getIdentifier(source_loc, "drawable", PACKAGE_NAME));
			} else {
				holder.source_flag.setImageResource(R.drawable.world_flag);
			}
			
			int num_docs = current_source.get_num_docs();
			holder.source_description.setText(num_docs + " articles");
			
			holder.source_checkbox.setTag(position);
			holder.source_checkbox.setOnCheckedChangeListener(null);
			
			final CheckBox.OnCheckedChangeListener listener = new CheckBox.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton cb, boolean flag) {
	                if (flag) {
	                    Integer pos = (Integer)cb.getTag();
	                    feed_sources.get(pos).set_selected(true);
	                } else {
	                	Integer pos = (Integer)cb.getTag();
	                    feed_sources.get(pos).set_selected(false);
	                }
	            }
			};
			holder.source_checkbox.setOnCheckedChangeListener(listener);
			
			if (current_source.is_selected()) {
				holder.source_checkbox.setChecked(true);
			} else {
				holder.source_checkbox.setChecked(false);
			}
			
			return view;
		}
		
	}

}
