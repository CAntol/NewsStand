package edu.umd.umiacs.newsstand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;

import edu.umd.umiacs.newsstand.Source.SourceType;


public class NewsStand extends MapActivity implements View.OnClickListener, Serializable, SensorEventListener {
	private SharedPreferences mPrefsSetting;
	private SharedPreferences mPrefsSources;
	private NewsStandMapView mMapView;
	private SeekBar mSlider;
	private Refresh mRefresh;
	private PopupPanel mPanel;
	private MarkerOverlay mOverlay;
	private ImageButton mButtonHome;
	private ImageButton mButtonLocal;
	private ImageButton mButtonWorld;
	private ImageButton mButtonLocate;
	private ImageButton mButtonMinus;
	private ImageButton mButtonPlus;
	private ImageButton mButtonRefresh;
	private ImageButton botFirstItem;
	private ImageButton botSecondItem;
	private ImageButton botThirdItem;
	private ImageButton botFourthItem;
	private ImageButton botFifthItem;
	private ImageButton botSixthItem;
	
	private TextView botFirstItemText;
	private TextView botSecondItemText;
	private TextView botThirdItemText;
	private TextView botFourthItemText;
	private TextView botFifthItemText;
	private TextView botSixthItemText;
	
	public String mSearchQuery;
	private LinearLayout mSearchLayout;
	private TextView mSearchView;
	
	private int screenHeight = 0, screenWidth = 0;
	private int mode;
	private int oneHand;
	public final static int NEWSSTAND = 0;
	public final static int TWITTERSTAND = 1;
	public final static int PHOTOSTAND = 2;
	
	private ArrayList<Source> feedSources;
	
	//Accelerometer
	private float lastX, lastY, lastZ;
	private boolean sensorInitialized;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private final float NOISE = (float) 2.0;
	
	private final static int SOURCES_RESULT = 12;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private Context getContext() {
		return this;
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		// TODO - fix the map problem
		//mMapView.zoomInMap();
		//mMapView.zoomOutMap();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// initialize main MapActivity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mode = NEWSSTAND;
		
		mOverlay = null;

		// initialize user preferences
		initPrefs();
		
		// initialize UI
		initMapView();
		initOneHand();
		initSlider();
		//initButtons();
		initPopupPanel();
		initSources();
		initAccelerometer();
		
		// handle search requests
		handleIntent(getIntent());
		
		// initialize Refresh processing object
		initRefresh();
		mMapView.setRefresh(mRefresh);
		
	}
	//resize map
	public void onWindowFocusChanged(boolean hasFocus) {
		//get screen height
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		//get size of topbar
		int topHeight = ((LinearLayout)findViewById(R.id.topbar)).getHeight();
		//get size of bottombar
		int botHeight = ((LinearLayout)findViewById(R.id.botbar)).getHeight();
		//set mapview size to height - topbar - bottombar
		LayoutParams p = mMapView.getLayoutParams();
		//this adjusts the height without making a call to setlayoutparams
		p.height = screenHeight - topHeight - botHeight;
		
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		initButtons();
	}
	
	private void initOneHand(){
		if (getResources().getBoolean(R.bool.isTablet))
			oneHand = 0;
		else
			oneHand = Integer.valueOf (mPrefsSetting.getString("one_handed", "0"));
	}
	
	public MarkerOverlay getOverlay() {
		return mOverlay;
	}
	
	public void setOverlay(MarkerOverlay overlay) {
		//if (mOverlay != null)
		//	mOverlay.hideAllBalloons();
		mOverlay = overlay;
	}
	
	public Refresh getRefresh() {
		return mRefresh;
	}
	
	public NewsStandMapView getMapView() {
		return mMapView;
	}
	
	public PopupPanel getPanel() {
		return mPanel;
	}
	
	public SharedPreferences getPrefsSetting() {
		return mPrefsSetting;
	}
	
	public SharedPreferences getPrefsSource() {
		return mPrefsSources;
	}
	
	public SeekBar getSlider() {
		return mSlider;
	}
	
	public ImageButton getHomeButton() {
		return mButtonHome;
	}
	
	public ImageButton getLocalButton() {
		return mButtonLocal;
	}
	
	public ImageButton getWorldButton() {
		return mButtonWorld;
	}
	
	public ImageButton getLocateButton() {
		return mButtonLocate;
	}
	
	public ImageButton getMinusButton() {
		return mButtonMinus;
	}
	
	public ImageButton getPlusButton() {
		return mButtonPlus;
	}
	
	public ImageButton getRefreshButton() {
		return mButtonRefresh;
	}
	/** load default or saved prefs into prefs object **/
	public void initPrefs() {
		mPrefsSetting = getSharedPreferences("preferences", 0);
		mPrefsSources = getSharedPreferences("sources", 0);
	}
	
	public ArrayList<Source> getFeedSources() {
		return feedSources;
	}
	
	public void setFeedSources(ArrayList<Source> list) {
		feedSources = list;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		super.onResume();
		initPrefs();
		//recreate the map to fix empty map display when started without internet
		initMapView();
		//refresh the preferences incase they were changed
		
		initOneHand();
		initButtons();
		
		//redraw
		mRefresh.clearSavedLocation();
		mapUpdateForce();
		
		//Accelerometer
	//	sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause() {
		super.onPause();
//		sensorManager.unregisterListener(this);
	}
	
	/** Delayed call to map refresh function.
	 *
	 *  Without delay, the refresh does not always happen. **/
	public void mapUpdateForce() {
		mapUpdateForce(250);
	}
	
	public void mapUpdateForce(int ms) {
		Handler mHandler = new Handler();
		mHandler.postDelayed(mRefresh, ms);
	}
	
	private void initMapView() {
		mMapView = (NewsStandMapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(false);
		mMapView.setNewHome(Boolean.parseBoolean(mPrefsSetting.getString("set_home", "false")));
	}
	
	private void initButtons(){
		mButtonHome = (ImageButton) findViewById(R.id.home);
		mButtonHome.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mMapView.goHome();
			}
		});
		
		mButtonLocal = (ImageButton) findViewById(R.id.local);
		mButtonLocal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mPanel.hide();
				mMapView.goToCurrentLocation();
			}
		});
		
		mButtonWorld = (ImageButton) findViewById(R.id.world);
		mButtonWorld.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mPanel.hide();
				mMapView.goWorld();
				
			}
		});
		
		mButtonLocate = (ImageButton) findViewById(R.id.locate);
		mButtonLocate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				locate();
			}
		});
		
		
		mButtonMinus = (ImageButton) findViewById(R.id.minus);
		mButtonMinus.setAlpha(128);
		mButtonMinus.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View v) {
				mMapView.zoomOutMap();
			}
			
		});
		
		mButtonPlus = (ImageButton) findViewById(R.id.plus);
		mButtonPlus.setAlpha(128);
		mButtonPlus.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				mMapView.zoomInMap();
			}
			
		});
		
		mButtonRefresh = (ImageButton) findViewById(R.id.refresh);
		mButtonRefresh.setAlpha(128);
		mButtonRefresh.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				mapUpdateForce(1000);
			}
			
		});
		
		botFirstItem = (ImageButton) findViewById(R.id.botFirstItem);
		botFirstItemText = (TextView) findViewById(R.id.botFirstItemText);
		botFirstItem.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent;
				
				//Info Neutral/Left ... Top Stories Right
				
				System.out.println("Button 1 one hand: " + oneHand);
				if (oneHand != 2) {
					intent = new Intent(v.getContext(), NewsStandWebView.class);
					intent = intent.putExtra("url", "http://www.cs.umd.edu/~hjs/newsstand-first-page.html");
				} else {
					if (mPanel != null && mRefresh != null)
						mPanel.hide();
					
					mMapView.updateMapWindow();
					intent = new Intent(v.getContext(), TopStories.class);
					
					intent.putExtra("mode", mode);
				}
				startActivity(intent);
			}
			
		});
		
		botSecondItem = (ImageButton) findViewById(R.id.botSecondItem);
		botSecondItemText = (TextView) findViewById(R.id.botSecondItemText);
		botSecondItem.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				
				//Search Neutral/Left ... Mode Right
				if (oneHand != 2) {
					onSearchRequested();
				} else {
					View modeItem = findViewById(R.id.botSecondItem);
					View modeText = findViewById(R.id.botSecondItemText);

					if (mode == 0) {
						mode = 1;
						((ImageButton) modeItem).setImageResource(R.drawable.camera);
						((TextView) modeText).setText("PhotoStand");
						((TextView) findViewById(R.id.current_mode)).setText("TwitterStand");
					} else if (mode == 1) {
						mode = 2;
						((ImageButton) modeItem).setImageResource(R.drawable.news_icon);
						((TextView) modeText).setText("NewsStand");
						((TextView) findViewById(R.id.current_mode)).setText("PhotoStand");
					} else if (mode == 2) {
						mode = 0;
						((ImageButton) modeItem).setImageResource(R.drawable.ic_action_bird);
						((TextView) modeText).setText("TwitterSstand");
						((TextView) findViewById(R.id.current_mode)).setText("NewsStand");
					}
					mRefresh.clearSavedLocation();
					mapUpdateForce();
				}
			}
			
		});
		
		botThirdItem = (ImageButton) findViewById(R.id.botThirdItem);
		botThirdItemText = (TextView) findViewById(R.id.botThirdItemText);
		botThirdItem.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent;
				
				// Settings Neutral/Left ... Sources Right
				if (oneHand != 2) {
					intent = new Intent(v.getContext(), Settings.class);
					intent.putExtra("ts", false);
					startActivity(intent);
				} else {
					intent = new Intent(v.getContext(), Sources.class);
					intent.putExtra("ts", false);
					intent.putExtra("feedSources", feedSources);
					startActivityForResult(intent, NewsStand.SOURCES_RESULT);
				}
			}
			
		});
		
		botFourthItem = (ImageButton) findViewById(R.id.botFourthItem);
		botFourthItemText = (TextView) findViewById(R.id.botFourthItemText);
		botFourthItem.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent;
				
				// Settings Neutral/Left ... Sources Right
				if (oneHand != 2) {
					intent = new Intent(v.getContext(), Sources.class);
					intent.putExtra("ts", false);
					intent.putExtra("feedSources", feedSources);
					startActivityForResult(intent, NewsStand.SOURCES_RESULT);
				} else {
					intent = new Intent(v.getContext(), Settings.class);
					intent.putExtra("ts", false);
					startActivity(intent);
				}
			}
			
		});
		botFifthItem = (ImageButton) findViewById(R.id.botFifthItem);
		botFifthItemText = (TextView) findViewById(R.id.botFifthItemText);
		botFifthItem.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View v) {
				
				//Mode Neutral/Left ... Search Right
				if (oneHand != 2) {
					View modeItem = findViewById(R.id.botFifthItem);
					View modeText = findViewById(R.id.botFifthItemText);

					if (mode == 0) {
						mode = 1;
						((ImageButton) modeItem).setImageResource(R.drawable.camera);
						((TextView) modeText).setText("PhotoStand");
						((TextView) findViewById(R.id.current_mode)).setText("TwitterStand");
					} else if (mode == 1) {
						mode = 2;
						((ImageButton) modeItem).setImageResource(R.drawable.news_icon);
						((TextView) modeText).setText("NewsStand");
						((TextView) findViewById(R.id.current_mode)).setText("PhotoStand");
					} else if (mode == 2) {
						mode = 0;
						((ImageButton) modeItem).setImageResource(R.drawable.ic_action_bird);
						((TextView) modeText).setText("TwitterSstand");
						((TextView) findViewById(R.id.current_mode)).setText("NewsStand");
					}
					mRefresh.clearSavedLocation();
					mapUpdateForce();
				} else {
					onSearchRequested();
				}
			}
		
		});
		
		botSixthItem = (ImageButton) findViewById(R.id.botSixthItem);
		botSixthItemText = (TextView) findViewById(R.id.botSixthItemText);
		botSixthItem.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent;
				
				//Top Stories Neutral/Left ... Info Right
				if (oneHand != 2) {
					if (mPanel != null && mRefresh != null)
						mPanel.hide();
					
					mMapView.updateMapWindow();
					intent = new Intent(v.getContext(), TopStories.class);
					
					intent.putExtra("mode", mode);
				} else {	
					intent = new Intent(v.getContext(), NewsStandWebView.class);
					intent = intent.putExtra("url", "http://www.cs.umd.edu/~hjs/newsstand-first-page.html");
				}
				startActivity(intent);
			}
			
		});
		
		View modeItem;
		View modeText;
		
		
		if (oneHand != 2) {
			botFirstItem.setImageResource(R.drawable.new_info);
			botFirstItemText.setText("Info");
			botSecondItem.setImageResource(R.drawable.new_search);
			botSecondItemText.setText("Search");
			botThirdItem.setImageResource(R.drawable.ic_menu_settings1);
			botThirdItemText.setText("Settings");
			botFourthItem.setImageResource(R.drawable.ic_menu_source);
			botFourthItemText.setText("Sources");
			modeItem = findViewById(R.id.botFifthItem);
			modeText = findViewById(R.id.botFifthItemText);
			botSixthItem.setImageResource(R.drawable.ic_menu_topstory);
			botSixthItemText.setText("Top Stories");
		} else {
			botFirstItem.setImageResource(R.drawable.ic_menu_topstory);
			botFirstItemText.setText("Top Stories");
			modeItem = findViewById(R.id.botSecondItem);
			modeText = findViewById(R.id.botSecondItemText);
			botThirdItem.setImageResource(R.drawable.ic_menu_source);
			botThirdItemText.setText("Sources");
			botFourthItem.setImageResource(R.drawable.ic_menu_settings1);
			botFourthItemText.setText("Settings");
			botFifthItem.setImageResource(R.drawable.new_search);
			botFifthItemText.setText("Search");
			botSixthItem.setImageResource(R.drawable.new_info);
			botSixthItemText.setText("Info");
		}

		if (mode == 0) {
			mode = 1;
			((ImageButton) modeItem).setImageResource(R.drawable.camera);
			((TextView) modeText).setText("PhotoStand");
			((TextView) findViewById(R.id.current_mode)).setText("TwitterStand");
		} else if (mode == 1) {
			mode = 2;
			((ImageButton) modeItem).setImageResource(R.drawable.news_icon);
			((TextView) modeText).setText("NewsStand");
			((TextView) findViewById(R.id.current_mode)).setText("PhotoStand");
		} else if (mode == 2) {
			mode = 0;
			((ImageButton) modeItem).setImageResource(R.drawable.ic_action_bird);
			((TextView) modeText).setText("TwitterSstand");
			((TextView) findViewById(R.id.current_mode)).setText("NewsStand");
		}
		mRefresh.clearSavedLocation();
		mapUpdateForce();
		 
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutButton);
		
		RelativeLayout.LayoutParams refreshParams = new RelativeLayout.LayoutParams(80,80);
		RelativeLayout.LayoutParams plusParams = new RelativeLayout.LayoutParams(80,80);
		RelativeLayout.LayoutParams minusParams = new RelativeLayout.LayoutParams(80,80);
		
		if (oneHand == 0){
			refreshParams.leftMargin = 0;
			refreshParams.topMargin = 0;
			plusParams.leftMargin = screenWidth-50;
			plusParams.topMargin = 0;
			minusParams.leftMargin = screenWidth-50;
			minusParams.topMargin = 100;
		}else if (oneHand == 1){
			refreshParams.leftMargin = refreshParams.topMargin = 0;
			plusParams.leftMargin = 350;
			plusParams.topMargin = 0;
			minusParams.leftMargin = 400;
			minusParams.topMargin = 100;
		} else {
			refreshParams.leftMargin = 400;
			refreshParams.topMargin = 0;
			plusParams.leftMargin = 50;
			plusParams.topMargin = 0;
			minusParams.leftMargin = 0;
			minusParams.topMargin = 100;
		}
		
		relativeLayout.removeView(mButtonRefresh);
		relativeLayout.addView(mButtonRefresh, refreshParams);
		relativeLayout.removeView(mButtonPlus);
		relativeLayout.addView(mButtonPlus, plusParams);
		relativeLayout.removeView(mButtonMinus);
		relativeLayout.addView(mButtonMinus, minusParams);
	}
	
	private void initSlider() {
		mSlider = (SeekBar) findViewById(R.id.slider);
		mSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				List<Overlay> mapOverlays = mMapView.getOverlays();
				if (mapOverlays.size() > 0) {
					MarkerOverlay o = (MarkerOverlay) mapOverlays.get(0);
					o.setPctShown(progress, getApplicationContext());
					mMapView.invalidate();
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
	}
	
	private void initPopupPanel() {
		mPanel = new PopupPanel(this, R.layout.popup_panel, mMapView, 0);
	}
	
	private void initRefresh() {
		mRefresh = new Refresh(this);
	}
	
	private void initSources() {
		
		if (feedSources == null) {
			feedSources = new ArrayList<Source>();
			/* Australia */
			feedSources.add(new Source("Sydney Morning Herald", 11019, SourceType.FEED_SOURCE));
			feedSources.add(new Source("The Age", 14003, SourceType.FEED_SOURCE));
			feedSources.add(new Source("The Australian", 114711, SourceType.FEED_SOURCE));
			/* Bahamas */
			feedSources.add(new Source("The Nassau Guardian", 17937, SourceType.FEED_SOURCE));
			/* United States */
			feedSources.add(new Source("Associated Press", 11063, SourceType.FEED_SOURCE));
			feedSources.add(new Source("Atlanta Journal-Constitution", 11770, SourceType.FEED_SOURCE));
			feedSources.add(new Source("New York Times", 156, SourceType.FEED_SOURCE));
		}
	}
	
	private void initAccelerometer() {
		sensorInitialized = false;
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		sensorInitialized = true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main_menu, menu);
//		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
	    super.onActivityResult(requestCode, resultCode, intent);
	    	
	    if (requestCode == SOURCES_RESULT) {
	    	if (resultCode == Activity.RESULT_OK) {
	    		Bundle extras = intent.getExtras();
	    		if (extras != null) {
	    			if (extras.get("feedSources") != null)
	    				feedSources = (ArrayList<Source>)extras.get("feedSources");
	    		}
	    	}
	    }

	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
				
			case R.id.search:
				onSearchRequested();
				break;        
			case R.id.settings:
				Intent i = new Intent(this, Settings.class);
				startActivity(i);
				break;
			case R.id.sources:
				Intent j = new Intent(this, Sources.class);
				startActivity(j);
				break;
			case R.id.top_stories:
				mMapView.updateMapWindow();
				Intent k = new Intent(this, TopStories.class);
				startActivity(k);
				break;
			case R.id.info:
				Intent l = new Intent(this, NewsStandWebView.class);
				l = l.putExtra("url", "http://www.cs.umd.edu/~hjs/newsstand-first-page.html");
				startActivity(l);
				// a web page , same as individual article. look at iphone code to get the url
				break;
			case R.id.mode:
				// switch between twitterstand and newsstand
				// break;
				
				//		default:
				//			Toast.makeText(getApplicationContext(),
				//					"Functionality not yet implemented now.", Toast.LENGTH_SHORT)
				//					.show();
		}
		return super.onOptionsItemSelected(item);
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
		search_view.setPadding(0, 0, 0, 100);
		mSearchLayout.addView(search_view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
																		 LinearLayout.LayoutParams.WRAP_CONTENT));
		
		// Instantiate an ImageView and define its properties
		ImageView mSearchView = new ImageView(this);
		mSearchView.setImageResource(R.drawable.ic_delete);
		mSearchView.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
		MarginLayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(3,6,0,0);
		mSearchView.setLayoutParams(lp);
		mSearchLayout.addView(mSearchView);
		
		mSearchLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				clearSearch();
			}
		});
		
		searchOptions.addView(mSearchLayout);
		mMapView.updateMapWindowForce();
	}
	
	public void clearSearch () {
		mSearchQuery = "";
		LinearLayout searchOptions = (LinearLayout)findViewById(R.id.search_options);
		searchOptions.removeView(mSearchLayout);
		//		Toast.makeText(this, "Search cleared.", Toast.LENGTH_SHORT).show();
		mMapView.updateMapWindowForce();
	}
	
	@Override
	public void onClick(View v) {
		if (v == mSearchView) {
			clearSearch();
		}
	}
	
	public void locate() {
		final EditText input = new EditText(this);
		
		new AlertDialog.Builder(this)
		.setTitle("Location Query")
		.setMessage("Enter location to go to:")
		.setView(input)
		.setPositiveButton("Go", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();
				//Toast.makeText(getContext(), "You searched for: " + value, Toast.LENGTH_SHORT).show();
				mPanel.hide();
				mMapView.goToPlace(value.toString());
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		}).show();
	}
	
	public int getMode() {
		return mode;
	}
	
	public void updateOneHand()
	{
		int oneHandNew = Integer.valueOf (mPrefsSetting.getString("one_handed", "0"));
		
		if (getResources().getBoolean(R.bool.isTablet))
			oneHandNew = 0;
		
		if (oneHandNew != oneHand)
		{
			oneHand = oneHandNew;
			
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutButton);
			
			RelativeLayout.LayoutParams refreshParams = new RelativeLayout.LayoutParams(80,80);
			RelativeLayout.LayoutParams plusParams = new RelativeLayout.LayoutParams(80,80);
			RelativeLayout.LayoutParams minusParams = new RelativeLayout.LayoutParams(80,80);
			
			View modeItem = null;
			View modeText = null;

			if (oneHand == 0){
				refreshParams.leftMargin = 0;
				refreshParams.topMargin = 0;
				plusParams.leftMargin = screenWidth-50;
				plusParams.topMargin = 0;
				minusParams.leftMargin = screenWidth-50;
				minusParams.topMargin = 100;
			}else if (oneHand == 1){
				refreshParams.leftMargin = refreshParams.topMargin = 0;
				plusParams.leftMargin = 350;
				plusParams.topMargin = 0;
				minusParams.leftMargin = 400;
				minusParams.topMargin = 100;
			} else {
				refreshParams.leftMargin = 400;
				refreshParams.topMargin = 0;
				plusParams.leftMargin = 50;
				plusParams.topMargin = 0;
				minusParams.leftMargin = 0;
				minusParams.topMargin = 100;
			}
			
			relativeLayout.removeView(mButtonRefresh);
			relativeLayout.addView(mButtonRefresh, refreshParams);
			relativeLayout.removeView(mButtonPlus);
			relativeLayout.addView(mButtonPlus, plusParams);
			relativeLayout.removeView(mButtonMinus);
			relativeLayout.addView(mButtonMinus, minusParams);
			
			if (oneHand != 2) {
				botFirstItem.setImageResource(R.drawable.new_info);
				botFirstItemText.setText("Info");
				botSecondItem.setImageResource(R.drawable.new_search);
				botSecondItemText.setText("Search");
				botThirdItem.setImageResource(R.drawable.ic_menu_settings1);
				botThirdItemText.setText("Settings");
				botFourthItem.setImageResource(R.drawable.ic_menu_source);
				botFourthItemText.setText("Sources");
				modeItem = findViewById(R.id.botFifthItem);
				modeText = findViewById(R.id.botFifthItemText);
				botSixthItem.setImageResource(R.drawable.ic_menu_topstory);
				botSixthItemText.setText("Top Stories");
			} else {
				botFirstItem.setImageResource(R.drawable.ic_menu_topstory);
				botFirstItemText.setText("Top Stories");
				modeItem = findViewById(R.id.botSecondItem);
				modeText = findViewById(R.id.botSecondItemText);
				botThirdItem.setImageResource(R.drawable.ic_menu_source);
				botThirdItemText.setText("Sources");
				botFourthItem.setImageResource(R.drawable.ic_menu_settings1);
				botFourthItemText.setText("Settings");
				botFifthItem.setImageResource(R.drawable.new_search);
				botFifthItemText.setText("Search");
				botSixthItem.setImageResource(R.drawable.new_info);
				botSixthItemText.setText("Info");
			}
			
			if (mode == 0) {
				((ImageButton) modeItem).setImageResource(R.drawable.ic_action_bird);
				((TextView) modeText).setText("TwitterStand");
				((TextView) findViewById(R.id.current_mode)).setText("NewsStand");
			} else if (mode == 1) {
				((ImageButton) modeItem).setImageResource(R.drawable.camera);
				((TextView) modeText).setText("PhotoStand");
				((TextView) findViewById(R.id.current_mode)).setText("TwitterStand");
			} else if (mode == 2) {
				((ImageButton) modeItem).setImageResource(R.drawable.news_icon);
				((TextView) modeText).setText("NewsStand");
				((TextView) findViewById(R.id.current_mode)).setText("PhotoStand");
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		
		if (x > 2.0) {
			SharedPreferences.Editor editor = mPrefsSetting.edit();
			editor.putString("one_handed", "1");
			editor.commit();
		} else if (x < -2.0){
			SharedPreferences.Editor editor = mPrefsSetting.edit();
			editor.putString("one_handed", "2");
			editor.commit();	
		}
		
		updateOneHand();
		
		
	}
	
}