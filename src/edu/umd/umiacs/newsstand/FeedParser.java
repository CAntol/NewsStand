package edu.umd.umiacs.newsstand;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class FeedParser {

	// names of the XML tags
	static final String RSS = "rss";
	static final String CHANNEL = "channel";
	static final String ITEM = "item";
	
	static final String DESCRIPTION = "description";
	static final String LAT = "latitude";
	static final String LON = "longitude";
	static final String LINK = "url";
	static final String TITLE = "title";
	static final String MARKUP = "markup";
	static final String SNIPPET = "snippet";
	
	private URL mFeedUrl;
	
	public FeedParser(String url){
		try {
			this.mFeedUrl = new URL(url);
		} catch (Exception e) {
			System.out.print("error in FeedParser constructor, url error");
			this.mFeedUrl = null;
		}	
	}
	
	protected InputStream getInputStream() {
		try {
			return mFeedUrl.openConnection().getInputStream();
		} catch (Exception e) {
			System.out.print("FeedParser getInputStream error!!!");
		}
		return null;
	}
	
	public List<Message> parse() {
		final Message currentMessage = new Message();
		RootElement root = new RootElement(RSS);
		final List<Message> messages = new ArrayList<Message>();
		Element itemlist = root.getChild(CHANNEL);
		Element item = itemlist.getChild(ITEM);
		item.setEndElementListener(new EndElementListener(){
			public void end() {
				messages.add(currentMessage.copy());
			}
		});
		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setTitle(body);
			}
		});
		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setLink(body);
			}
		});
		item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setDescription(body);
			}
		});
		item.getChild(MARKUP).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body){
				currentMessage.setMarkup(body);
			}
		});
		item.getChild(SNIPPET).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body){
				currentMessage.setSnippet(body);
			}
		});
		item.getChild(LAT).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body){
				currentMessage.setLat(body);
			}
		});
		item.getChild(LON).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body){
				currentMessage.setLon(body);
			}
		});
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messages;
	}
}
