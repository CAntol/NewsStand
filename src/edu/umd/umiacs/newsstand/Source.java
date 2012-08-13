package edu.umd.umiacs.newsstand;

import java.io.Serializable;

public class Source implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum SourceType {
		ALL_SOURCES, FEED_SOURCE, COUNTRY_SOURCE, LANGUAGE_SOURCE, BOUND_SOURCE
	}
	
	private String _name = null;
	private String _lang_code = null;
	private String _source_location = null;
	
	private int _feed_link = 0;
	private SourceType _sourceType;
	
	private boolean _selected = false;
	private int _num_docs = 0;
	
	public Source () {}
	
	public Source (String name, int feed_link, SourceType sourceType) {
		this.set_name(name);
		this.set_feed_link(feed_link);
		this.set_sourceType(sourceType);
	}
	
	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_source_location() {
		return _source_location;
	}

	public void set_source_location(String _source_location) {
		this._source_location = _source_location;
	}

	public String get_lang_code() {
		return _lang_code;
	}

	public void set_lang_code(String _lang_code) {
		this._lang_code = _lang_code;
	}

	public int get_feed_link() {
		return _feed_link;
	}

	public void set_feed_link(int _feed_link) {
		this._feed_link = _feed_link;
	}

	public SourceType get_sourceType() {
		return _sourceType;
	}

	public void set_sourceType(SourceType _sourceType) {
		this._sourceType = _sourceType;
	}

	public int get_num_docs() {
		return _num_docs;
	}

	public void set_num_docs(int _num_docs) {
		this._num_docs = _num_docs;
	}

	public boolean is_selected() {
		return _selected;
	}

	public void set_selected(boolean _selected) {
		this._selected = _selected;
	}

	
}
