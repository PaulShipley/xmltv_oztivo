package name.paulshipley.xmltv;

import name.paulshipley.Common.Debug;

/**
 * The Class Constants.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Constants.java,v 1.4 2010/01/05 12:25:51 paul Exp $
 */
public class Constants {

	/** The Constant for the Base URL. */
	public static final String BASE_URL = (Debug.DEBUG) ? 
			new String("http://localhost/xmltv/") : new String("http://xml.oztivo.net/xmltv/");

	/** The Constant Selected Channels file. */
	public static final String SELECTED = new String("selected_channels.xml");

	/** The Constant Server File Manager file. */
	public static final String SFMFile = new String("serverfile.ser");

	/** The Constant XMLTV Schema file. */
	public static final String SCHEMA = new String("xmltv.xsd");

	/** The Constant XMLTV Output file. */
	public static final String XMLTV = new String("xmltv.xml");

	/** The Constant Channels XML file. */
	public static final String CHANNEL = new String("channels.xml");

	/** The Constant Datalist XML file. */
	public static final String DATALIST = new String("datalist.xml");

	/** The Constant Channels Transform file. */
	public static final String CHANNEL_TRANSFORM = new String("channels.xsl");

	/** The Constant Programme Transform file. */
	public static final String PROGRAMME_TRANSFORM = new String("programme.xsl");

	/** The Constant for XML file extentsions. */
	public static final String XML = new String(".xml");

	/** The Constant for Compressed file extentsions. */
	public static final String COMPRESSED = new String(".gz");

	/** The Constant SAVE. */
	public static final String SAVE = new String("Save");

	/** The Constant PROCESS. */
	public static final String PROCESS = new String("Process");

	/** The Constant QUIT. */
	public static final String QUIT = new String("Quit");

	/** The Constant ABOUT. */
	public static final String ABOUT = new String("About");

}
