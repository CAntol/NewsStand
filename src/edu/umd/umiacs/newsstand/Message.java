package edu.umd.umiacs.newsstand;

/**
 * This is adapted from 
 * http://www.warriorpoint.com/blog/2009/07/19/android-simplified-source-code-for-parsing-and-working-with-xml-data-and-web-services-in-android/
 * 
 * Message is the element on ClusterViewer, each message is one url link
 * */

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


// message - one link to 
public class Message implements Comparable<Message>{
	private SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	private String _title;
	private URL _link;
	private String _description;
	private Date _date;
	private String _snippet;
	private String _markup;
	private float _lat;
	private float _lon;
	
	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title.trim();
	}
	// getters and setters omitted for brevity 
	public URL getLink() {
		return _link;
	}

	public void setLink(String link) {
		try {
			this._link = new URL(link);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description.trim();
	}

	public String getDate() {
		return FORMATTER.format(this._date);
	}

	public void setDate(String date) {
		// pad the date if necessary
		while (!date.endsWith("00")){
			date += "0";
		}
		try {
			this._date = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setSnippet(String snippet) {
		this._snippet = snippet;
	}
	
	public String getSnipper(){
		return this._snippet;
	}
	
	public void setMarkup(String markup){
		this._markup = markup;
	}
	
	public String getMarkup(){
		return this._markup;
	}
	

	public void setLat(String body) {
		_lat = Float.valueOf(body);
	}
	
	public float getLat(){
		return _lat;
	}

	public void setLon(String body) {
		_lon = Float.valueOf(body);
	}
	
	public float getLon(){
		return _lon;
	}
	
	public Message copy(){
		Message copy = new Message();
		copy._title = _title;
		copy._link = _link;
		copy._description = _description;
		copy._date = _date;
		copy._markup=_markup;
		copy._snippet=_snippet;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append(_title);
		sb.append('\n');
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append('\n');
		sb.append("Link: ");
		sb.append(_link);
		sb.append('\n');
		sb.append("Description: ");
		sb.append(_description);
		sb.append("Markup: ");
		sb.append(_markup);
		sb.append("Snippet: ");
		sb.append(_snippet);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_date == null) ? 0 : _date.hashCode());
		result = prime * result
				+ ((_description == null) ? 0 : _description.hashCode());
		result = prime * result + ((_link == null) ? 0 : _link.hashCode());
		result = prime * result + ((_title == null) ? 0 : _title.hashCode());
		result = prime * result + ((_markup == null)? 0 : _markup.hashCode());
		result = prime * result + ((_snippet == null)? 0 : _snippet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (_date == null) {
			if (other._date != null)
				return false;
		} else if (!_date.equals(other._date))
			return false;
		if (_description == null) {
			if (other._description != null)
				return false;
		} else if (!_description.equals(other._description))
			return false;
		if (_link == null) {
			if (other._link != null)
				return false;
		} else if (!_link.equals(other._link))
			return false;
		if (_title == null) {
			if (other._title != null)
				return false;
		} else if (!_title.equals(other._title))
			return false;
		if (_markup == null){
			if(other._markup != null)
				return false;
		} else if (!_markup.equals(other._markup))
			return false;
		if(_snippet == null){
			if(other._snippet != null)
				return false;
		} else if(!_snippet.equals(other._snippet))
			return false;
		return true;
	}

	public int compareTo(Message another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another._date.compareTo(_date);
	}


}

