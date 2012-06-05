package edu.umd.umiacs.newsstand;

import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import edu.umd.umiacs.newsstand.MarkerOverlay.MarkerOverlayItem;

public class TopStoriesRefresh implements Runnable {

	// references to other class member variables
	private final TopStories _ctx;
	private final TopStoriesMapView _mMapView;
	private final SeekBar _mSlider;
	private final Resources _mResources;

	// member variables
	private int mNumExecuting = 0;
	private int mShowIdx = 0;
	private int mAjaxIdx = 0;

	private int mLatL = 0;
	private int mLatH = 0;
	private int mLonL = 0;
	private int mLonH = 0;

	private String cluster_id;
	private boolean _fClickCell;

	public TopStoriesRefresh(Context ctx) {
		_ctx = (TopStories) ctx;
		_mMapView = _ctx.getMapView();
		_mSlider = _ctx.getSlider();
		_mResources = _ctx.getResources();
	}

	@Override
	public void run() {
		executeForce();
	}

	public void execute() {
		if (mNumExecuting < 3) {
			if (curBoundsDiffer()) {
				updateBounds();
				new RefreshTask().execute("");
			}
		}
	}

	public void executeForce() {
		updateBounds();
		new RefreshTask().execute("");
	}

	public void clearSavedLocation() {
		mLatL = 0;
		mLatH = 0;
		mLonL = 0;
		mLonH = 0;
	}

	private boolean curBoundsDiffer() {
		GeoPoint centerpoint = _mMapView.getMapCenter();
		int lat_span = _mMapView.getLatitudeSpan();
		int lon_span = _mMapView.getLongitudeSpan();

		int lat_l = centerpoint.getLatitudeE6() - (lat_span / 2);
		int lat_h = lat_l + lat_span;
		int lon_l = centerpoint.getLongitudeE6() - (lon_span / 2);
		int lon_h = lon_l + lon_span;

		return (lat_l != mLatL || lat_h != mLatH || lon_l != mLonL || lon_h != mLonH);
	}

	public void updateBounds() {
		GeoPoint centerpoint = _mMapView.getMapCenter();
		int lat_span = _mMapView.getLatitudeSpan();
		int lon_span = _mMapView.getLongitudeSpan();

		mLatL = centerpoint.getLatitudeE6() - (lat_span / 2);
		mLatH = mLatL + lat_span;
		mLonL = centerpoint.getLongitudeE6() - (lon_span / 2);
		mLonH = mLonL + lon_span;

	}

	public MarkerFeed getMarkers() {
		String marker_url = "";
		if (_ctx.getMode() == 1) {
			marker_url = "http://twitterstand.umiacs.umd.edu/news/xml_map?cluster_id=";
		} else
			marker_url = "http://newsstand.umiacs.umd.edu/news/xml_map?cluster_id=";

		marker_url+=cluster_id;

		return getFeed(marker_url);
	}



	private void setMarkers(MarkerFeed feed) {
		List<Overlay> mapOverlays = _mMapView.getOverlays();

		MarkerOverlay itemizedoverlay = new MarkerOverlay( 
				_mResources.getDrawable(R.drawable.marker_general), _ctx);

		for (int i = 0; i < feed.getMarkerCount(); i++) {
			MarkerInfo cur_marker = feed.getMarker(i);
			float lat = 0.0f;
			if(! cur_marker.getLatitude().contains("-"));
			lat = Float.valueOf(cur_marker.getLatitude()).floatValue();
			float lon = 0.0f;
			if( ! cur_marker.getLongitude().contains("-"));
			lon = Float.valueOf(cur_marker.getLongitude()).floatValue();
			GeoPoint point = new GeoPoint( (int) ( lat* 1E6), (int) ( lon* 1E6));

			MarkerOverlayItem overlayitem = new MarkerOverlayItem(point,
					cur_marker.getTitle(), cur_marker.getSnippet(), cur_marker.getGazID(),
					cur_marker.getName());

			String cur_topic = cur_marker.getTopic();

			int my_marker = 0;

			if (cur_topic.equals("General"))
				my_marker = R.drawable.marker_general;
			else if (cur_topic.equals("Business"))
				my_marker = R.drawable.marker_business;
			else if (cur_topic.equals("Entertainment"))
				my_marker = R.drawable.marker_entertainment;
			else if (cur_topic.equals("Health"))
				my_marker = R.drawable.marker_health;
			else if (cur_topic.equals("SciTech"))
				my_marker = R.drawable.marker_scitech;
			else if (cur_topic.equals("Sports"))
				my_marker = R.drawable.marker_sports;
			else {
				my_marker = R.drawable.marker_general;
				Toast.makeText(_ctx, "Bad topic: " + cur_topic, Toast.LENGTH_SHORT).show();
			}
			itemizedoverlay.addOverlay(overlayitem, _mResources.getDrawable(my_marker));
		}


		if (feed.getMarkerCount() > 0) {
			itemizedoverlay.setPctShown(_mSlider.getProgress(), _ctx);
			if (mapOverlays.size() > 0) {
				mapOverlays.set(0,itemizedoverlay);
			}
			else {
				mapOverlays.add(itemizedoverlay);
			}
			_mMapView.invalidate();
			if(_fClickCell)
				resetBound();
		}

	}

	private MarkerFeed getFeed(String urlToRssFeed){
		try {
			// set up the url
			URL url = new URL(urlToRssFeed);

			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// instantiate our handler
			MarkerFeedHandler theMarkerFeedHandler = new MarkerFeedHandler();
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
						//Toast.makeText(_ctx, "Error fetching markers",
								//Toast.LENGTH_SHORT).show();
			
				return null;
				
			////////////TEMP TEST CODE ////////////////
/*			MarkerFeed tempFeed = new MarkerFeed();
			MarkerInfo tempInfo = new MarkerInfo();
			tempInfo.setClusterID("1");
			tempInfo.setDescription("description");
			tempInfo.setTitle("title");

			tempInfo.setTopic("general_topics");
			tempFeed.addItem(tempInfo);

			////////////TEMP TEST CODE ////////////////
			return tempFeed;
*/
		}
	}


	public void setClusterID(String id){
		cluster_id = id;
		this.executeForce();
	}

	public class RefreshTask extends AsyncTask<String, Integer, MarkerFeed> {

		private int refresh_idx;

		@Override
		protected MarkerFeed doInBackground(String... string) {
			mNumExecuting++;
			mAjaxIdx++;
			refresh_idx = mAjaxIdx;
			return getMarkers();
		}

		@Override
		protected void onPostExecute(MarkerFeed feed) {
			if (feed != null) {
				if (refresh_idx > mShowIdx) {
					mShowIdx = refresh_idx;
					setMarkers(feed);
				}
				mNumExecuting--;
			} else {
				Toast.makeText(_ctx, "Unable to access internet", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void resetBound() {
		MarkerOverlay overlay= (MarkerOverlay) _mMapView.getOverlays().get(0);
		_mMapView.getController().zoomToSpan(overlay.getLatSpanE6(), overlay.getLonSpanE6());
		GeoPoint center = overlay.getMiddlePoint();
		_mMapView.getController().animateTo(center);
		_fClickCell = false;
	}

	public void isClick() {
		_fClickCell = true;
	}

}
