package edu.umd.umiacs.newsstand;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MarkerFeedHandler extends DefaultHandler {

    private MarkerFeed _mFeed;
    private MarkerInfo _mMarker;

    private final int RSS_TITLE = 1;
    private final int RSS_LATITUDE = 2;
    private final int RSS_LONGITUDE = 3;
    private final int RSS_NAME = 4;
    private final int RSS_DESCRIPTION = 5;
    private final int RSS_GAZ_ID = 6;
    private final int RSS_TOPIC = 7;
    private final int RSS_CLUSTER_ID = 8;
    private final int RSS_MARKUP = 9;
    private final int RSS_SNIPPET = 10;
    private final int RSS_KEYWORD = 11;

    int depth = 0;
    int currentstate = 0;
    /*
     * Constructor
     */
    MarkerFeedHandler()
    {
    }

    /*
     * getFeed - this returns our feed when all of the parsing is complete
     */
    MarkerFeed getFeed()
    {
        return _mFeed;
    }


    @Override
    public void startDocument() throws SAXException
    {
        // initialize our MarkerFeed object - this will hold our parsed contents
        _mFeed = new MarkerFeed();
        // initialize the MarkerInfo object - we will use this as a crutch to grab the info from the channel
        // because the channel and items have very similar entries..
        _mMarker = new MarkerInfo();
    }

    @Override
    public void endDocument() throws SAXException
    {
    }

    @Override
    public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException
    {
        depth++;
        if (localName.equals("channel"))
        {
            currentstate = 0;
            return;
        }
        //if (localName.equals("image"))
        //{
        //    // record our feed data - we temporarily stored it in the item :)
        //    _feed.setTitle(_item.getTitle());
        //    _feed.setPubDate(_item.getPubDate());
        //}
        if (localName.equals("item"))
        {
            // create a new item
            _mMarker = new MarkerInfo();
            return;
        }
        if (localName.equals("title"))
        {
            currentstate = RSS_TITLE;
            return;
        }
        if (localName.equals("latitude"))
        {
            currentstate = RSS_LATITUDE;
            return;
        }
        if (localName.equals("longitude"))
        {
            currentstate = RSS_LONGITUDE;
            return;
        }
        if (localName.equals("name"))
        {
            currentstate = RSS_NAME;
            return;
        }
        if (localName.equals("description"))
        {
            currentstate = RSS_DESCRIPTION;
            return;
        }
        if (localName.equals("gaz_id"))
        {
            currentstate = RSS_GAZ_ID;
            return;
        }
        if (localName.equals("gaz_id"))
        {
            currentstate = RSS_GAZ_ID;
            return;
        }
        if (localName.equals("topic"))
        {
            currentstate = RSS_TOPIC;
            return;
        }
        if (localName.equals("cluster_id"))
        {
            currentstate = RSS_CLUSTER_ID;
            return;
        }
        if (localName.equals("marker"))
        {
            currentstate = RSS_MARKUP;
            return;
        }
        if (localName.equals("snippet"))
        {
            currentstate = RSS_SNIPPET;
            return;
        }
        if (localName.equals("keyword"))
        {
        	currentstate = RSS_KEYWORD;
        	return;
        }

        // if we don't explicitly handle the element, make sure we don't wind up erroneously
        // storing a newline or other bogus data into one of our existing elements
        currentstate = 0;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {
        depth--;
        if (localName.equals("item"))
        {
            // add our item to the list!
            _mFeed.addItem(_mMarker);
            return;
        }
    }

    @Override
    public void characters(char ch[], int start, int length)
    {
        String theString = new String(ch, start, length);

        switch (currentstate)
        {
            case RSS_TITLE:
                _mMarker.setTitle(theString);
                currentstate = 0;
                break;
            case RSS_LATITUDE:
                _mMarker.setLatitude(theString);
                currentstate = 0;
                break;
            case RSS_LONGITUDE:
                _mMarker.setLongitude(theString);
                currentstate = 0;
                break;
            case RSS_NAME:
                _mMarker.setName(theString);
                currentstate = 0;
                break;
            case RSS_DESCRIPTION:
                _mMarker.setDescription(theString);
                currentstate = 0;
                break;
            case RSS_GAZ_ID:
                _mMarker.setGazID(theString);
                currentstate = 0;
                break;
            case RSS_TOPIC:
                _mMarker.setTopic(theString);
                currentstate = 0;
                break;
            case RSS_CLUSTER_ID:
                _mMarker.setClusterID(theString);
                currentstate = 0;
                break;
            case RSS_MARKUP:
                _mMarker.setMarkup(theString);
                currentstate = 0;
                break;
            case RSS_SNIPPET:
                _mMarker.setSnippet(theString);
                currentstate = 0;
                break;
            case RSS_KEYWORD:
            	_mMarker.setKeyword(theString);
            	break;
            default:
                return;
        }
    }
}
