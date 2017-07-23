package name.paulshipley.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import name.paulshipley.Common.ExceptionHandler;

/**
 * The Class ServerFileManager. <br/>
 * Manages the list of SeverFile objects that represent each of the files to be
 * downloaded from the remote server
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ServerFileManager.java,v 1.9 2010/03/29 11:56:25 paul Exp $
 */
public class ServerFileManager {
	private final File SFfile = new File(Constants.SFMFile);
	private static ServerFileManager instance = new ServerFileManager();
	private HashMap<String, ServerFile> serverfilelist;

	/**
	 * Instantiate this Singleton.
	 */
	private ServerFileManager() {
		super();

		try {
			if (!this.SFfile.exists()) {
				serverfilelist = new HashMap<String, ServerFile>();
				this.save();
			} else {
				this.load();
			}
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}
	}

	/**
	 * Get this Instance (Singleton).
	 * 
	 * @return the server file manager instance
	 */
	public static ServerFileManager instance() {
		return instance;
	}

	/**
	 * Adds the server file to the manager list and gets the file from the
	 * server.
	 * 
	 * @param sf
	 *            the ServerFile object to be added
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public synchronized void add(ServerFile sf) throws IOException,
			InterruptedException {
		String filename = sf.getFileName();
		if (serverfilelist.containsKey(filename)) {
			// ServerFile exists on list, update Base_URL and download from
			// server
			ServerFile mapsf = serverfilelist.get(filename);
			mapsf.setBase_url(sf.getBase_url());
			mapsf.getGZFileFromServer();
		} else {
			// unknown ServerFile, download from server and add to list
			sf.getGZFileFromServer();
			serverfilelist.put(filename, sf);
		}

		this.save();
	}

	/**
	 * Removes a server file from the list and deletes the local copy.
	 * 
	 * @param sf
	 *            the sf
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public synchronized void remove(ServerFile sf) throws IOException,
			InterruptedException {
		String filename = sf.getFileName();
		if (serverfilelist.containsKey(filename)) {
			// ServerFile exists on list, remove
			ServerFile mapsf = serverfilelist.get(filename);
			mapsf.removeLocalFile();
			serverfilelist.remove(filename);
		}

		this.save();
	}

	/**
	 * Removes expired files.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ParseException
	 */
	public synchronized void removeExpired() throws ParseException,
			IOException, InterruptedException {
		ArrayList<ServerFile> al = new ArrayList<ServerFile>();

		Iterator<Entry<String, ServerFile>> sfpit = this.serverfilelist
				.entrySet().iterator();
		while (sfpit.hasNext()) {
			ServerFile sf = sfpit.next().getValue();
			String datafor = sf.getDatafor();
			// channel and datalist files have null values for datafor
			if (datafor != null) {
				if (!Utility.isFutureDate(Utility.StringToDate(datafor))) {
					// build temp list of files to be removed
					al.add(sf);
				}
			}
		}

		Iterator<ServerFile> alit = al.iterator();
		while (alit.hasNext()) {
			ServerFile sf = alit.next();
			// remove all files on temp list
			System.out.println("remove " + sf.getFileName());
			this.remove(sf);
		}
	}

	/**
	 * Persist the manager list.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void save() throws IOException {
		FileOutputStream fos = new FileOutputStream(SFfile);
		ObjectOutputStream os = new ObjectOutputStream(fos);

		os.writeObject(serverfilelist);
		os.close();
	}

	/**
	 * Load the persistent list into this manager.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	@SuppressWarnings("unchecked")
	private void load() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(SFfile);
		ObjectInputStream is = new ObjectInputStream(fis);

		serverfilelist = (HashMap<String, ServerFile>) (is.readObject());
		is.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerFileProcessor [SFfile=" + SFfile + ", serverfilelist="
				+ serverfilelist + "]";
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the command line arguments
	 */
	public static void main(String[] argv) {
		try {
			ServerFileManager sfp = new ServerFileManager();
			sfp.add(new ServerFile(Constants.CHANNEL, Utility
					.DateToString(Utility.today()), Constants.BASE_URL));
			sfp.add(new ServerFile(Constants.DATALIST, Utility
					.DateToString(Utility.today()), Constants.BASE_URL));

			System.out.println("save");

			sfp.toString();

			Iterator<Entry<String, ServerFile>> sfpit = sfp.serverfilelist
					.entrySet().iterator();
			Entry<String, ServerFile> sf;
			while (sfpit.hasNext()) {
				sf = sfpit.next();
				System.out.println(sf.getValue().toString());
			}

			System.out.println("exit");
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
