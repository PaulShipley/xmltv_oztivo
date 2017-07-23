package name.paulshipley.xmltv;

/**
 * The Class Datalist.
 * Object to represent each of the data sources.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Datalist.java,v 1.3 2009/12/28 11:58:23 paul Exp $
 */
public class Datalist {
	private String id;
	private String display_name;
	private String base_url;
	private String datafor;
	private String lastmodified;

	/**
	 * Instantiates a new datalist.
	 */
	public Datalist() {
		super();

		this.id = null;
		this.display_name = null;
		this.base_url = null;
		this.datafor = null;
		this.lastmodified = null;
	}

	/**
	 * Instantiates a new datalist.
	 * 
	 * @param id
	 *            the channel id
	 * @param display_name
	 *            the display_name
	 * @param base_url
	 *            the base_url
	 * @param datafor
	 *            the datafor date ("2009-11-16")
	 * @param lastmodified
	 *            the lastmodified date ("20091116133325 +1000")
	 */
	public Datalist(String id, String display_name, String base_url,
			String datafor, String lastmodified) {
		super();

		this.id = id;
		this.display_name = display_name;
		this.base_url = base_url;
		this.datafor = datafor;
		this.lastmodified = lastmodified;
	}

	/**
	 * Gets the channel id.
	 * 
	 * @return the channel id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the channel id.
	 * 
	 * @param id
	 *            the channel id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the display_name.
	 * 
	 * @return the display_name
	 */
	public String getDisplay_name() {
		return display_name;
	}

	/**
	 * Sets the display_name.
	 * 
	 * @param display_name
	 *            the display_name to set
	 */
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	/**
	 * Gets the base_url.
	 * 
	 * @return the base_url
	 */
	public String getBase_url() {
		return base_url;
	}

	/**
	 * Sets the base_url.
	 * 
	 * @param base_url
	 *            the base_url to set
	 */
	public void setBase_url(String base_url) {
		this.base_url = base_url;
	}

	/**
	 * Gets the datafor date.
	 * 
	 * @return the datafor
	 */
	public String getDatafor() {
		return datafor;
	}

	/**
	 * Sets the datafor date.
	 * 
	 * @param datafor
	 *            the datafor to set
	 */
	public void setDatafor(String datafor) {
		this.datafor = datafor;
	}

	/**
	 * Gets the lastmodified date.
	 * 
	 * @return the lastmodified
	 */
	public String getLastmodified() {
		return lastmodified;
	}

	/**
	 * Sets the lastmodified date.
	 * 
	 * @param lastmodified
	 *            the lastmodified to set
	 */
	public void setLastmodified(String lastmodified) {
		this.lastmodified = lastmodified;
	}

	/**
	 * Gets the filename of the local copy.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return new String(id + "_" + datafor + Constants.XML);
	}

	/**
	 * Gets the url of the file on the server.
	 * 
	 * @return the datafor
	 */
	public String getURL() {
		return new String(base_url + getFilename() + Constants.COMPRESSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Datalist [id=" + id + ", display_name=" + display_name
				+ ", base_url=" + base_url + ", datafor=" + datafor
				+ ", lastmodified=" + lastmodified + ", Filename="
				+ getFilename() + ", URL=" + getURL() + "]";
	}
}
