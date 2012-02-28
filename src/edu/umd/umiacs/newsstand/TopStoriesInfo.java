package edu.umd.umiacs.newsstand;

public class TopStoriesInfo {


    //title, latitude, longitude, name, description, gaz_id, topic, cluster_id, markup, snippet
    
    private String _title = null;
    private String _description = null;
    private String _domain = null;
    private String _url = null;
    private String _time = null;
    private String _topic = null;
    private String _cluster_id = null;
    private String _num_docs = null;
    private String _num_images = null;
    private String _num_videos = null;
    
    TopStoriesInfo()
    {
    }
    
    public String getTitle() {
		return _title;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String _description) {
		this._description = _description;
	}

	public String getDomain() {
		return _domain;
	}

	public void setDomain(String _domain) {
		this._domain = _domain;
	}

	public String getURL() {
		return _url;
	}

	public void setURL(String _url) {
		this._url = _url;
	}

	public String getTime() {
		return _time;
	}

	public void setTime(String _time) {
		this._time = _time;
	}

	public String getTopic() {
		return _topic;
	}

	public void setTopic(String _topic) {
		this._topic = _topic;
	}

	public String getCluster_id() {
		return _cluster_id;
	}

	public void setCluster_id(String _cluster_id) {
		this._cluster_id = _cluster_id;
	}

	public String getNum_docs() {
		return _num_docs;
	}

	public void setNum_docs(String _num_docs) {
		this._num_docs = _num_docs;
	}

	public String getNum_images() {
		return _num_images;
	}

	public void setNum_images(String _num_images) {
		this._num_images = _num_images;
	}

	public String getNum_videos() {
		return _num_videos;
	}

	public void setNum_videos(String _num_videos) {
		this._num_videos = _num_videos;
	}

	public String toString()
    {
        // limit how much text we display
        if (_title.length() > 42)
        {
            return _title.substring(0, 42) + "...";
        }
        return _title;
    }
}
