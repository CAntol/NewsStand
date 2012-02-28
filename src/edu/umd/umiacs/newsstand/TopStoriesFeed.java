package edu.umd.umiacs.newsstand;

import java.util.List;
import java.util.Vector;

public class TopStoriesFeed {

    private String _mTitle = null;
    private String _mPubdate = null;
    private int _mInfocount = 0;
    private List<TopStoriesInfo> _mInfolist;
    
    
    TopStoriesFeed()
    {
        _mInfolist = new Vector<TopStoriesInfo>(0); 
    }
    int addItem(TopStoriesInfo info)
    {
        _mInfolist.add(info);
        _mInfocount++;
        return _mInfocount;
    }
    TopStoriesInfo getNews(int location)
    {
        return _mInfolist.get(location);
    }
    List<TopStoriesInfo> getAllInfos()
    {
        return _mInfolist;
    }
    int getInfoCount()
    {
        return _mInfocount;
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
