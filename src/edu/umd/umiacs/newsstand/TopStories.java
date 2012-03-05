package edu.umd.umiacs.newsstand;

import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;



public class TopStories extends MapActivity implements View.OnClickListener{

	private SharedPreferences _mPrefs;
	private TopStoriesMapView _mMapView;
	private TopStoriesListView _mListView;
	private SeekBar _mSlider;
	private TopStoriesRefresh _mRefresh;
	private PopupPanel _mPanel;
	private TopStoriesFeed _mFeed;

	public String mSearchQuery;
	private LinearLayout mSearchLayout;
	private TextView mSearchView;
	
	boolean fSetHome;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// initialize main MapActivity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topstory);

		// initialize user preferences
		initPrefs();

		// initialize UI
		initMapView();
		initSlider();
        initPopupPanel();        
        initFeed();
        initListView();
        
        // initialize Refresh processing object
        initRefresh();
    	_mMapView.setRefresh(_mRefresh);

        // handle search requests
        handleIntent(getIntent());
        
        fSetHome = _mPrefs.getBoolean("set_home", false);        
	}

    public TopStoriesRefresh getRefresh() {
        return _mRefresh;
    }

    public SharedPreferences getPrefs(){
		return _mPrefs;
	}
    public TopStoriesMapView getMapView() {
        return _mMapView;
    }
	 
    public PopupPanel getPanel() {
        return _mPanel;
    }

    public SharedPreferences refs() {
        return _mPrefs;
    }

    public SeekBar getSlider() {
        return _mSlider;
    }

    /** load default or saved prefs into prefs object **/
	public void initPrefs() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		_mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

	/** Handle incoming intents **/
   private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            	addSearch(query);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        _mRefresh.clearSavedLocation();
        mapUpdateForce();
    }
    
	/** Delayed call to map refresh function.
	 *
	 *  Without delay, the refresh does not always happen. **/
	
    public void mapUpdateForce() {
        mapUpdateForce(250);
    }

    public void mapUpdateForce(int ms) {
    	Handler mHandler = new Handler();
        mHandler.postDelayed(_mRefresh, ms);
    }
    
	private void initMapView() {
		_mMapView = (TopStoriesMapView) findViewById(R.id.topStoryView);
		_mMapView.setBuiltInZoomControls(false);
	}

	///////////////////////////////////////////
	private void initSlider() {	
		_mSlider = (SeekBar) findViewById(R.id.topStorySlider);
        _mSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                List<Overlay> mapOverlays = _mMapView.getOverlays();
                if (mapOverlays.size() > 0) {
                    MarkerOverlay o = (MarkerOverlay) mapOverlays.get(0);
                    o.setPctShown(progress, getApplicationContext());
                    _mRefresh.resetBound();
                    _mMapView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
	/////////////////////////////////////////

	private void initPopupPanel() {
        _mPanel = new PopupPanel(this, R.layout.popup_panel, _mMapView, 1);
    }
	 
	    private void initRefresh() {
        _mRefresh = new TopStoriesRefresh(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return false;
    } 

    public void addSearch(String query) {
        mSearchQuery = query;
        LinearLayout searchOptions = (LinearLayout)findViewById(R.id.search_options);

        mSearchLayout = new LinearLayout(this);
        mSearchLayout.setOrientation(LinearLayout.HORIZONTAL);
        mSearchLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        TextView search_view = new TextView(this);
        search_view.setText("Search: " + query);
        search_view.setTextColor(Color.BLUE);
        search_view.setTextSize(16);
        mSearchLayout.addView(search_view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                         LinearLayout.LayoutParams.WRAP_CONTENT));

        // Instantiate an ImageView and define its properties
        ImageView mSearchView = new ImageView(this);
        mSearchView.setImageResource(R.drawable.ic_delete);
        mSearchView.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        MarginLayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(3,6,0,0);
        mSearchView.setLayoutParams(lp);
        mSearchView.setOnClickListener(this);
        mSearchLayout.addView(mSearchView);

        searchOptions.addView(mSearchLayout);
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
        _mMapView.updateMapWindowForce();
    }
    
    public void clearSearch () {
        mSearchQuery = "";
        LinearLayout searchOptions = (LinearLayout)findViewById(R.id.search_options);
        searchOptions.removeView(mSearchLayout);
        Toast.makeText(this, "Search cleared.", Toast.LENGTH_SHORT).show();
        _mMapView.updateMapWindowForce();
    }
	

	@Override
	public void onClick(View v) {
		if(v == mSearchView)
			clearSearch();
	}
	
    public TopStories getContext() {
        return this;
    }
   
    public void initListView(){
    	_mListView = (TopStoriesListView) findViewById (R.id.topStoryList);
    	if(_mFeed != null)
    		_mListView.initAdapter(_mFeed);
    }
    
	public SharedPreferences getPrefsSetting() {
		return _mPrefs;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public TopStoriesListView getListView() {
		return _mListView;
	}

	public void updateMapView(String cluster_id) {
		_mRefresh.setClusterID(cluster_id);
		
	}
	

	
	///////////////////////////////////
	private TopStoriesFeed getFeed() {
		try {
			String topStory = "http://newsstand.umiacs.umd.edu/news/iphone_results?a=5";
			
			if (this.mSearchQuery != null && this.mSearchQuery != "") {
				topStory += String.format("&search=%s", this.mSearchQuery);
			}

			// sourcesParam - TODO - need to implement

			// rankParam - TODO - three types: &rank=time , &rank=newest , &rank=twitter . - need to implement

			// topicParam - DONE
			topStory += topicQuery();

			// imagesParam - num of images
			topStory += imageQuery();
			// videosParam - num of videos
			topStory += videoQuery();
			
			// boundParam TODO
			// countryParam TODO
			
			// set up the url
			URL url = new URL(topStory);

			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// instantiate our handler
			TopStoriesHandler theMarkerFeedHandler = new TopStoriesHandler();
			// assign our handler
			xmlreader.setContentHandler(theMarkerFeedHandler);
			// get our data via the url class
			InputSource is = new InputSource(url.openStream());
			// perform the synchronous parse
			xmlreader.parse(is);
			// get the results - should be a fully populated RSSFeed instance,
			// or null on error
			return theMarkerFeedHandler.getFeed();
			
		} catch (Exception ee) {
			// if we have a problem, simply return null
			//TODO - remove temp code
			//Toast.makeText(this, "Error fetching markers",
			//		Toast.LENGTH_SHORT).show();
			return null;
			
			////////////TEMP TEST CODE ////////////////
/*
			TopStoriesFeed tempFeed = new TopStoriesFeed();
			TopStoriesInfo tempInfo = new TopStoriesInfo();
			tempInfo.setCluster_id("1");
		    tempInfo.setDescription("description");
		    tempInfo.setTitle("title");
		    
		    tempInfo.setTopic("general_topics");
		    tempFeed.addItem(tempInfo);
		    
		    TopStoriesInfo two = new TopStoriesInfo();
		    two.setCluster_id("1");
		    two.setDescription("description: description should wrap around since it could be supper duper upper long long long tilte of this news that will never ever end and will continue like this forever and ever");
		    two.setTitle("title: supper duper upper long long long tilte of this news that will never ever end and will continue like this forever and ever");
		    
		    two.setTopic("general_topics");
		    tempFeed.addItem(two);
		    
		    ////////////TEMP TEST CODE ////////////////
			return tempFeed;
*/
		}
	}
	
	
	private String videoQuery(){
		String video = _mPrefs.getString("videos", "0");
		int videoInt = Integer.valueOf(video);
		if(videoInt != 0)
			return String.format("&num_videos=%s", video);
		return "";
	}

	private String imageQuery(){
		String image = _mPrefs.getString("images", "0");
		int imageInt = Integer.valueOf(image);
		if(imageInt != 0)
			return String.format("&num_images=%s", image);
		return "";
	}
	

	private String topicQuery() {
		if (_mPrefs.getBoolean("all_topics", false)) {
			// add nothing to query string if showing all topics
		}
		else {
			String topics = "";
			if (_mPrefs.getBoolean("general_topics", false)) {
				topics += "'General',";
			}
			if (_mPrefs.getBoolean("business_topics", false)) {
				topics += "'Business',";
			}
			if (_mPrefs.getBoolean("scitech_topics", false)) {
				topics += "'SciTech',";
			}
			if (_mPrefs.getBoolean("entertainment_topics", false)) {
				topics += "'Entertainment',";
			}
			if (_mPrefs.getBoolean("health_topics", false)) {
				topics += "'Health',";
			}
			if (_mPrefs.getBoolean("sports_topics", false)) {
				topics += "'Sports',";
			}
			if (topics.length() > 0) {
				return String.format("&cat=(%s)", topics.substring(0, topics.length()-1));
			}
		}
		return "";
	}

	private void initFeed(){
		_mFeed = getFeed();
	}
 
//	@Override
//	public void onBackPressed(){
//	}
}
