package name.paulshipley.xmltv;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import name.paulshipley.Common.ExceptionHandler;
import name.paulshipley.Common.XML.StateAwareHandler;
import name.paulshipley.Common.XML.TagHandler;

import org.xml.sax.SAXException;

/**
 * The Class DataListParser. Loads the list of available programme files from
 * the datalist xml file for the selected channels.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: DataListParser.java,v 1.4 2010/01/04 09:28:55 paul Exp $
 */
public class DataListParser {
	private File xml;
	private List<String> selected;

	private List<Datalist> datalist;

	private boolean channel_valid = false;
	private String channel = new String("");
	private String display_name = new String("");
	private String base_url = new String("");
	private String datafor = new String("");
	private String lastmodified = new String("");

	/**
	 * Instantiates a new Datalist XML parser.
	 */
	public DataListParser() {
		super();

		this.xml = null;
		this.selected = null;
	}

	/**
	 * Instantiates a new Datalist XML parser.
	 * 
	 * @param xml
	 *            the datalist xml file
	 * @param selected
	 *            the selected channel list
	 */
	public DataListParser(File xml, List<String> selected) {
		super();

		this.xml = xml;
		this.selected = selected;
	}

	/**
	 * Sets the xml.
	 * 
	 * @param xml
	 *            the datalist xml file to set
	 */
	public void setXml(File xml) {
		this.xml = xml;
	}

	/**
	 * Sets the selected channel list.
	 * 
	 * @param selected
	 *            the selected
	 */
	public void setSelected(List<String> selected) {
		this.selected = selected;
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
			// check if current channel id is on the selected channel list
			channel = this.getAttrib("id");
			if (selected.contains(channel)) {
				channel_valid = true;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#end()
		 */
		@Override
		public void end() {
			channel_valid = false;
		}
	}

	/**
	 * The Class DisplayNameTag.<br/>
	 * Handles
	 * <code> &lt;display-name lang="en"&gt;13th Street&lt;/display-name&gt; </code>
	 */
	private class DisplayNameTag extends TagHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#tagName()
		 */
		@Override
		public String tagName() {
			return "display-name";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#characters(java.lang.String)
		 */
		@Override
		public void characters(String s) {
			display_name = s;
		}
	}

	/**
	 * The Class BaseURLTag.<br/>
	 * Handles
	 * <code> &lt;base-url&gt;http://www.oztivo.net/xmltv/&lt;/base-url&gt; </code>
	 */
	private class BaseURLTag extends TagHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#tagName()
		 */
		@Override
		public String tagName() {
			return "base-url";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#characters(java.lang.String)
		 */
		@Override
		public void characters(String s) {
			base_url = s;
		}
	}

	/**
	 * The Class DataForTag.<br/>
	 * Handles
	 * <code> &lt;datafor lastmodified="20091116133325 +1000"&gt;2009-11-16&lt;/datafor&gt; </code>
	 */
	private class DataForTag extends TagHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#tagName()
		 */
		@Override
		public String tagName() {
			return "datafor";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#start()
		 */
		@Override
		public void start() {
			lastmodified = this.getAttrib("lastmodified");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#characters(java.lang.String)
		 */
		@Override
		public void characters(String s) {
			datafor = s;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#start()
		 */
		@Override
		public void end() {
			if (channel_valid) {
				try {
					if (Utility.isFutureDate(Utility.StringToDate(datafor))) {
						datalist.add(new Datalist(channel, display_name,
								base_url, datafor, lastmodified));
					}
				} catch (ParseException e) {
					ExceptionHandler.handleAndTerminate(e);
				}
			}
		}
	}

	/**
	 * Parses the.
	 * 
	 * @param SelectedChannels
	 *            the selected channels list
	 * 
	 * @return the datalist
	 * 
	 * @throws SAXException
	 *             the SAX exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<Datalist> getDatalist(List<String> SelectedChannels)
			throws ParserConfigurationException, SAXException, IOException {
		assert this.xml != null : "datalist xml file is null";
		assert this.selected != null : "selected channel list is null";

		// the datalist to be populated
		datalist = new ArrayList<Datalist>();

		// Configure the SAX event handler
		StateAwareHandler handler = new StateAwareHandler();
		handler.addTagHandler(new DataListParser.ChannelTag());
		handler.addTagHandler(new DataListParser.DisplayNameTag());
		handler.addTagHandler(new DataListParser.BaseURLTag());
		handler.addTagHandler(new DataListParser.DataForTag());

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();

		// Parse the input
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(this.xml, handler);

		return datalist;
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the command line arguments
	 */
	public static void main(String[] argv) {
		try {
			List<String> sc = new ArrayList<String>();
			sc.add("ABC-Vic");
			sc.add("31-Mel");
			sc.add("13THST");

			System.out.println("get datalist");
			DataListParser dlp = new DataListParser();
			dlp.setXml(new File("datalist.xml")); //$NON-NLS-1$
			dlp.setSelected(sc);
			List<Datalist> dll = dlp.getDatalist(sc);

			System.out.println("print datalist");
			Iterator<Datalist> it = dll.iterator();
			while (it.hasNext()) {
				Datalist dl = it.next();
				System.out.println(dl.toString());
			}
			System.out.println("Done.");
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
