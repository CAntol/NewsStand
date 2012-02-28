package edu.umd.umiacs.newsstand;

import java.util.List;
import java.util.Vector;

public class MarkerFeed {

    private String _mTitle = null;
    private String _mPubdate = null;
    private int _mMarkercount = 0;
    private List<MarkerInfo> _mMarkerlist;
    
    
    MarkerFeed()
    {
        _mMarkerlist = new Vector<MarkerInfo>(0); 
    }
    int addItem(MarkerInfo marker)
    {
        _mMarkerlist.add(marker);
        _mMarkercount++;
        return _mMarkercount;
    }
    MarkerInfo getMarker(int location)
    {
        return _mMarkerlist.get(location);
    }
    List<MarkerInfo> getAllMarkers()
    {
        return _mMarkerlist;
    }
    int getMarkerCount()
    {
        return _mMarkercount;
    }
    void setTitle(String title)
    {
        _mTitle = title;
    }
    void setPubDate(String pubdate)
    {
        _mPubdate = pubdate;
    }
    String getTitle()
    {
        return _mTitle;
    }
    String getPubDate()
    {
        return _mPubdate;
    }
        
}
