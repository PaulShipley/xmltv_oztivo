package name.paulshipley.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import name.paulshipley.Common.ExceptionHandler;
import name.paulshipley.Common.XML.StateAwareHandler;
import name.paulshipley.Common.XML.TagHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class ChannelParser <br/>
 * Populate the ChannelList with the channels contained in the XML description.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ChannelParser.java,v 1.8 2010/01/04 09:28:55 paul Exp $
 */
public class ChannelParser {
	private File xml;
	private ChannelList channellist;

	private Channel ch;

	/**
	 * Instantiates a new channel parser.
	 */
	public ChannelParser() {
		super();

		this.xml = null;
		this.channellist = null;
	}

	/**
	 * Instantiates a new channel parser.
	 * 
	 * @param xml
	 *            the xml
	 * @param channellist
	 *            the channellist
	 */
	public ChannelParser(File xml, ChannelList channellist) {
		super();

		this.xml = xml;
		this.channellist = channellist;
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
	 * Sets the channellist.
	 * 
	 * @param channellist
	 *            the new channellist
	 */
	public void setChannellist(ChannelList channellist) {
		this.channellist = channellist;
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
			ch = new Channel();
			ch.setId(this.getAttrib("id"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see name.paulshipley.xmltv.TagHandler#end()
		 */
		@Override
		public void end() {
			channellist.add(ch);
		}
	}

	/**
	 * The Class DisplayNameTag.<br/>
	 * Handles
	 * <code> &lt;display-name lang="en"&gt;SelectTV 1&lt;/display-name&gt; </code>
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
			ch.setDisplay_name(s.trim());
		}
	}

	/**
	 * Parses the channels xml to create the channellist.
	 * 
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void parse() throws ParserConfigurationException, SAXException,
			IOException {
		// validate configuration
		assert this.channellist != null : "list is null";
		assert this.xml != null : "xml file is null";

		// Configure the SAX event handler
		StateAwareHandler handler = new StateAwareHandler();
		handler.addTagHandler(new ChannelParser.ChannelTag());
		handler.addTagHandler(new ChannelParser.DisplayNameTag());

		// Define validation
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		StreamSource xsd = new StreamSource(ChannelParser.class
				.getResourceAsStream("xmltv.xsd"));
		Schema schema = sf.newSchema(xsd);
		Validator validator = schema.newValidator();

		// Define input as xml file
		InputStream in = new FileInputStream(xml);
		InputSource is = new InputSource(in);
		SAXSource saxIn = new SAXSource(is);

		// Output to my handler
		SAXResult saxOut = new SAXResult(handler);

		// Parse and validate
		validator.validate(saxIn, saxOut);
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the command line arguments
	 */
	public static void main(String[] argv) {
		try {
			ChannelList chl = new ChannelList();
			ChannelParser cp = new ChannelParser(new File(Constants.CHANNEL),
					chl);
			cp.parse();

			Iterator<Channel> cmit = chl.iterator();
			while (cmit.hasNext()) {
				System.out.println(cmit.next().toString());
			}

		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
