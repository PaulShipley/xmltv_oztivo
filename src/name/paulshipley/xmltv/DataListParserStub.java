package name.paulshipley.xmltv;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.paulshipley.Common.ExceptionHandler;

/**
 * Test stub
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: DataListParserStub.java,v 1.1 2009/12/28 11:58:23 paul Exp $
 */
public class DataListParserStub {
	private File xml;
	private String channel;
	private List<Datalist> datalist;

	private String base_url;

	/**
	 * Instantiates a new data list parser test.
	 */
	public DataListParserStub() {
		super();

		this.datalist = null;
	}

	/**
	 * @param xml_in
	 *            the xml to set
	 */
	public void setXml(File xml_in) {
		this.xml = xml_in;
	}

	/**
	 * @param channel_in
	 *            the channel to set
	 */
	public void setChannel(String channel_in) {
		this.channel = channel_in;
	}

	/**
	 * @param datalist
	 *            the filelist to set
	 */
	public void setDatalist(List<Datalist> datalist) {
		this.datalist = datalist;
	}

	/**
	 * Parses the.
	 */
	public void parse() {
		String[] names = { "2009-11-22", "2009-11-23" };
		base_url = new String(Constants.BASE_URL);

		if (channel.equals("GO") || channel.equals("Nine-Mel")) {
			for (int i = 0; i < names.length; i++) {
				String lastmodified = "20091122135404 +1000";
				String display_name = channel;
				datalist.add(new Datalist(channel, display_name, base_url,
						names[i], lastmodified));
			}
		}
	}

	/**
	 * The main method.
	 * 
	 * @param argv the argv
	 */
	public static void main(String[] argv) {
		List<Datalist> datalist = new ArrayList<Datalist>();

		try {
			DataListParserStub dpInst = new DataListParserStub();
			dpInst.setXml(new File("datalist.xml"));
			dpInst.setChannel("Nine-Mel");
			dpInst.setDatalist(datalist);
			dpInst.parse();

			System.out.println(dpInst.xml.getAbsolutePath());

			Iterator<Datalist> it = datalist.iterator();
			while (it.hasNext()) {
				Datalist dl = it.next();
				System.out.println(dl.toString());
			}
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
