package name.paulshipley.xmltv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import name.paulshipley.Common.ExceptionHandler;

import org.xml.sax.SAXException;

/**
 * Process xmltv files from oztivo to generate a program guide for PVRs.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: DatalistProcessor.java,v 1.8 2010/03/29 11:56:25 paul Exp $
 */
public class DatalistProcessor extends Observable {
	private List<String> channellist;
	private List<Datalist> datalist;
	private TransformerFactory TFact = TransformerFactory.newInstance();
	private FileOutputStream xmltv_fs;
	private ServerFileManager sfm = ServerFileManager.instance();

	/**
	 * Logs progress message
	 * 
	 * @param msg the msg
	 */
	private void logmsg(String msg) {
		System.out.println(msg);
		setChanged();
		notifyObservers(msg);
	}
	
	/**
	 * Gets the current datalist xml file from the server.
	 * 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void getCurrentDatalist() throws IOException, InterruptedException, ParseException {
		logmsg("get datalist from server");
		// use null for datefor value as datalist files do not expire
		sfm.add(new ServerFile(Constants.DATALIST, null, Constants.BASE_URL));
	}

	/**
	 * Gets the selected channels.
	 * 
	 * @return the selected channels
	 * 
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ArrayList<String> getSelectedChannels()
			throws ParserConfigurationException, SAXException, IOException {
		logmsg("get saved selected channels");

		SelectedChannelParser scp = new SelectedChannelParser();
		scp.setXml(new File(Constants.SELECTED));
		ArrayList<String> sl = scp.getSelectedList();
		
		return sl;
	}

	/**
	 * get list of files.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 *             the SAX exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	private List<Datalist> getListOfFiles(List<String> channellist)
			throws ParserConfigurationException, SAXException, IOException {
		logmsg("get list of files");

		DataListParser dlp = new DataListParser();
		dlp.setXml(new File(Constants.DATALIST)); 
		dlp.setSelected(channellist);
		List<Datalist> dll = dlp.getDatalist(channellist);

		return dll;
	}

	/**
	 * get files from server.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void getFilesFromServer() throws IOException, InterruptedException {
		Iterator<Datalist> datait = datalist.iterator();
		while (datait.hasNext()) {
			Datalist dl = datait.next();
			logmsg("get from sever " + dl.getFilename());

			sfm.add(new ServerFile(dl.getFilename(), dl.getDatafor(), dl.getBase_url()));
		}
	}

	/**
	 * extract channels for selected channels.
	 * 
	 * @throws TransformerException
	 *             the transformer exception
	 */
	private void extractChannels(List<String> channellist)
			throws TransformerException {
		InputStream StyleSheet = DatalistProcessor.class
				.getResourceAsStream(Constants.CHANNEL_TRANSFORM);
		Transformer TForm = TFact.newTransformer(new StreamSource(StyleSheet));

		logmsg("extracting channels");

		Iterator<String> channelit = channellist.iterator();
		while (channelit.hasNext()) {
			String channel = channelit.next();
			TForm.reset();
			TForm.setParameter("channel", channel);
			TForm.transform(new StreamSource(Constants.CHANNEL),
					new StreamResult(xmltv_fs));
		}
	}

	/**
	 * extract programmes for selected channels.
	 * 
	 * @throws TransformerException
	 *             the transformer exception
	 */
	private void extractProgrammes(List<Datalist> datalist)
			throws TransformerException {
		InputStream StyleSheet = DatalistProcessor.class
				.getResourceAsStream(Constants.PROGRAMME_TRANSFORM);
		Transformer TForm = TFact.newTransformer(new StreamSource(StyleSheet));

		logmsg("extract programmes");

		Iterator<Datalist> fileit = datalist.iterator();
		while (fileit.hasNext()) {
			Datalist dl = fileit.next();
			String filename = dl.getFilename();
			TForm.reset();
			TForm.transform(new StreamSource(filename), new StreamResult(
					xmltv_fs));
		}
	}

	/**
	 * Removes expired datalist files.
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void removeFiles() throws ParseException, IOException, InterruptedException {
		logmsg("removing old files");
		sfm.removeExpired();
	}

	/**
	 * Clean up.
	 */
	private void cleanUp() {
		logmsg("finished");
	}

	/**
	 * Generate.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void generate() throws IOException, TransformerException,
			ParserConfigurationException, SAXException, InterruptedException, ParseException {
		xmltv_fs = new FileOutputStream(Constants.XMLTV);
		PrintWriter xmltv_pw = new PrintWriter(xmltv_fs);

		// get current data list
		getCurrentDatalist();

		// get selected channels
		channellist = getSelectedChannels();

		// get list of files
		datalist = getListOfFiles(this.channellist);

		// get files from server
		getFilesFromServer();

		// write root element
		xmltv_pw.println("<tv generator-info-name=\"" + Strings.getString("FullName") + "\">");
		xmltv_pw.flush();

		// extract channels for selected channels
		extractChannels(this.channellist);

		// extract programmes from downloaded files
		extractProgrammes(this.datalist);

		// close output stream
		xmltv_pw.println("</tv>");
		xmltv_pw.flush();

		xmltv_fs.flush();
		xmltv_fs.close();

		// clean up and remove old files
		removeFiles();
		cleanUp();
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the argv
	 */
	public static void main(String[] argv) {
		try {
			DatalistProcessor dlp = new DatalistProcessor();
			dlp.generate();
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
