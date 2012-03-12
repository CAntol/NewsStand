package edu.umd.umiacs.newsstand;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class NewsStandMapView extends MapView {
    private final NewsStand _ctx;
    private Refresh _refresh;

    private long lastTouchTime = -1;
    private LocationManager locationManager;
    private MapController mapController;
    private GeoPoint home;
    private int latSpan;
    private int longSpan;

    public boolean fSetHome;
    private int oneHand;
    
    public NewsStandMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _ctx = (NewsStand)context;

        initLocate();

        //Resources resources = _ctx.getResources();
        //Drawable drawable = resources.getDrawable(R.drawable.marker_general);
        //getOverlays().add(new MarkerOverlay(drawable, context));
    }
    
    public NewsStand getCurContext(){
    	return _ctx;
    }
    
    public void initfSetHome(boolean setHome){
    	fSetHome = setHome;
    }
    
    public int getOneHand(){
    	return oneHand;
    }
    
    public void initOneHand(int OneHand){
    	oneHand = OneHand;
    }
    
    public void setHome(){
    	home = this.getMapCenter();
    	latSpan = this.getLatitudeSpan();
    	longSpan = this.getLongitudeSpan();
    }
    
    public GeoPoint getHome(){
    	return home;
    }
    
    public void clearHome(){
    	home = null;
    	latSpan = longSpan = 0;
    }

    public void setRefresh(Refresh refresh_instance) {
        _refresh = refresh_instance;
    }

    private void initLocate() {
        locationManager = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);
        mapController = this.getController();
    }

    // based on example on p262 in Android Developer's Cookbook
    public void goToPlace(String location_query) {

        List<Address> addresses;
        Geocoder gc = new Geocoder(_ctx);

        try {
            addresses = gc.getFromLocationName(location_query, 1);
            if (addresses != null) {
                Address x = addresses.get(0);
                int lat = (int) (x.getLatitude() * 1E6);
                int lng = (int) (x.getLongitude() * 1E6);
                //Toast.makeText(_ctx, x.getExtras().toString(), Toast.LENGTH_SHORT).show();
                goToLocation(new GeoPoint(lat, lng));
                mapController.setZoom(10);
            }
        } catch(IOException e) {
            Toast.makeText(_ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goToCurrentLocation() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER
        // do : telnet localhost 5554 , then do geo fix 32.15 52.34
        
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation == null) {
        	lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	if (lastKnownLocation != null) {
        		goToLocation(lastKnownLocation);
                mapController.setZoom(14);
        	}
        	
        	//here we use GPS to get current location or use less accurate means
        	locationManager = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                	goToLocation(location);
                    mapController.setZoom(14);
                }
                public void onProviderDisabled(String provider) {
                	Toast.makeText(_ctx, "You must first enable Location Services", Toast.LENGTH_SHORT).show();
                }
                public void onProviderEnabled(String provider) {}
                public void onStatusChanged(String provider, int status, Bundle extras) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.removeUpdates(locationListener);
        } else {
        	goToLocation(lastKnownLocation);
            mapController.setZoom(14);
        }
    }

    public void goToLocation(Location loc) {
    	if (loc == null)
    		return;
    	int lat = (int) (loc.getLatitude() * 1E6);
        int lng = (int) (loc.getLongitude() * 1E6);
        goToLocation(new GeoPoint(lat, lng));
    }

    public void goToLocation(GeoPoint loc) {
        mapController.animateTo(loc);
        _ctx.mapUpdateForce(1000);
    }
    
    public void goWorld(){
    	int latSpan = 150000000;
    	int lonSpan = 80000000;
    	mapController.zoomToSpan(latSpan, lonSpan);
    	_ctx.mapUpdateForce(1000);
    }

    public void goHome(){
    	if(fSetHome)
    	{
    		goToLocation(home);
    		mapController.zoomToSpan(latSpan, longSpan);
    	}
    	else
    		goWorld();
    }
    
    public void zoomInMap(){
		mapController.zoomIn();
//		int lat = this.getLatitudeSpan() * 0.8;
//		int lon = this.getLongtitudeSpan() * 0.8;
//		mapController.zoomToSpan(latSpanE6, lonSpanE6);
		_ctx.mapUpdateForce(1000);
    }
    
    public void zoomOutMap(){
		mapController.zoomOut();
//		mapController.zoomToSpan(latSpanE6, lonSpanE6);
		_ctx.mapUpdateForce(1000);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (_ctx.getPanel() == null || _refresh == null) {
            return super.onInterceptTouchEvent(ev);
        }
        else {
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

    public void updateMapWindowForce() {
        if (_refresh == null) {
            Toast.makeText(_ctx, "Refresh object is null.  Can't refresh", Toast.LENGTH_SHORT).show();
        } else {
            _refresh.executeForce();
        }
    }

	public void updateHome(boolean fSetHomeNew) {
		// set fSetHome
        if(fSetHomeNew && !fSetHome)
        {
        	fSetHome = true;
        	setHome();
        } 
        else if (!fSetHomeNew && fSetHome)
        {
        	fSetHome = false;
        	clearHome();
        }
	}
}
