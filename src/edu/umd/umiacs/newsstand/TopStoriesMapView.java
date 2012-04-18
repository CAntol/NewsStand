package edu.umd.umiacs.newsstand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class TopStoriesMapView extends MapView{

	private final TopStories _ctx;
	private MapController _mMapController;

	private TopStoriesRefresh _refresh;

	private long lastTouchTime = -1;

	public TopStoriesMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		_ctx = (TopStories)context;
		_mMapController = this.getController();


	}

	public void setRefresh(TopStoriesRefresh mRefresh) {
		_refresh = mRefresh;
	}

	public void goToLocation(GeoPoint loc) {
		_mMapController.animateTo(loc);
		_ctx.mapUpdateForce(1000);
	}

	public void updateMapWindowForce() {
		if (_refresh == null) {
			Toast.makeText(_ctx, "Refresh object is null.  Can't refresh", Toast.LENGTH_SHORT).show();
		} else {
			_refresh.executeForce();
		}

	}
	
	public void zoomInMap(){
		_mMapController.zoomIn();
		_ctx.mapUpdateForce(1000);
    }
    
    public void zoomOutMap(){
    	_mMapController.zoomOut();
		_ctx.mapUpdateForce(1000);
    }

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (_ctx.getPanel() == null || _refresh == null) {
			return super.onInterceptTouchEvent(ev);
		} else {
			_ctx.getPanel().hide();
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			long thisTime = System.currentTimeMillis();
			if (thisTime - lastTouchTime < 250) {

				// Double tap
				this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
				lastTouchTime = -1;

			} else {

				// Too slow :)
				lastTouchTime = thisTime;
			}
		}

		boolean t = super.onInterceptTouchEvent(ev);
		updateMapWindow();
		return t;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction()==MotionEvent.ACTION_UP) {
			updateMapWindow();
		}
		return super.onTouchEvent(ev);
	}

	public void updateMapWindow() {
		if (_refresh == null) {
			Toast.makeText(_ctx, "Refresh object is null.  Can't refresh", Toast.LENGTH_SHORT).show();
		} else {
			_refresh.execute();
		}
	}
}
