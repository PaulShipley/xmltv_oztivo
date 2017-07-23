package name.paulshipley.xmltv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import name.paulshipley.Common.ExceptionHandler;
import name.paulshipley.Common.XML.StateAwareHandler;
import name.paulshipley.Common.XML.TagHandler;

import org.xml.sax.SAXException;

/**
 * The Class SelectedChannelParser <br/>
 * Loads, maintains and persists the selected channel list.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: SelectedChannelParser.java,v 1.7 2010/01/04 09:28:55 paul Exp $
 */
public class SelectedChannelParser {
	private File xml;
	private ArrayList<String> channellist = new ArrayList<String>();

	/**
	 * Instantiates a new selected channel parser.
	 */
	public SelectedChannelParser() {
		super();

		this.xml = null;
	}

	/**
	 * Instantiates a new selected channel parser.
	 * 
	 * @param xml
	 *            the xml
	 */
	public SelectedChannelParser(File xml) {
		super();

		this.xml = xml;
	}

	/**
	 * Sets the xml.
	 * 
	 * @param xml
	 *            the new xml
	 */
	public void setXml(File xml) {
		this.xml = xml;
	}

	/**
	 * The Class ChannelTag.<br/>
	 * Handles <code> &lt;channel id="STV1"/&gt; </code>
	 */
	private class ChannelTag extends TagHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#tagName()
		 */
		@Override
		public String tagName() {
			return "channel";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#start()
		 */
		@Override
		public void start() {
			channellist.add(this.getAttrib("id"));
		}
	}

	/**
	 * Parses the selected channels xml to create the channellist.
	 * 
	 * @return the list of selected channel ids
	 * 
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ArrayList<String> getSelectedList()
			throws ParserConfigurationException, SAXException, IOException {
		// validate configuration
		assert this.xml != null : "xml file is null";

		channellist.clear();

		// Configure the SAX event handler
		StateAwareHandler handler = new StateAwareHandler();
		handler.addTagHandler(new SelectedChannelParser.ChannelTag());

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();

		// Parse the input
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(this.xml, handler);

		return channellist;
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the command line arguments
	 */
	public static void main(String[] argv) {
		try {
			SelectedChannelParser scp = new SelectedChannelParser();
			scp.setXml(new File("selected_channels.xml"));
			ArrayList<String> sl = scp.getSelectedList();

			Iterator<String> channelit = sl.iterator();
			while (channelit.hasNext()) {
				System.out.println(channelit.next().toString());
			}
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
