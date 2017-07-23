package name.paulshipley.xmltv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import name.paulshipley.Common.ExceptionHandler;

/**
 * The Class ServerFile <br/>
 * 
 * Each SeverFile object represents a file to be downloaded from the remote
 * server <br/>
 * The process to download the xmltv files from the oztvio server is described
 * here
 * <code>http://www.oztivo.net/twiki/bin/view/TVGuide/StaticXMLGuideAPI</code>.
 * There are a number of requirements to reduce the load on the servers, so
 * please read this document carefully before changing this code.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ServerFile.java,v 1.11 2010/03/23 08:54:14 paul Exp $
 */
public class ServerFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private String filename;
	private String datafor;
	private String base_url;
	private String etag;
	private String lastmodified;
	private int response_code;

	/**
	 * Instantiates a new server file
	 * 
	 * @param filename
	 *            the filename
	 * @param datafor
	 *            the date this file is for
	 * @param base_url
	 *            the base_url
	 */
	public ServerFile(String filename, String datafor, String base_url) {
		super();

		this.filename = filename;
		this.datafor = datafor;
		this.base_url = base_url;
		this.etag = null;
		this.lastmodified = null;
	}

	/**
	 * Gets the file name
	 * 
	 * @return the filename
	 */
	public String getFileName() {
		return filename;
	}

	/**
	 * Gets the datafor.
	 * 
	 * @return the datafor
	 */
	public String getDatafor() {
		return datafor;
	}

	/**
	 * Gets the base_url
	 * 
	 * @return the base_url
	 */
	public String getBase_url() {
		return base_url;
	}

	/**
	 * Sets the base_url
	 * 
	 * @param base_url
	 *            the base_url
	 */
	public void setBase_url(String base_url) {
		if (!this.base_url.equals(base_url)) {
			// Base_URL has changed, adjust saved value
			this.base_url = base_url;

			// invalidate etag & lastmodified as server has changed
			this.etag = null;
			this.lastmodified = null;
		}
	}

	/**
	 * Get compressed file from server, expand and save locally
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void getGZFileFromServer() throws IOException, InterruptedException {
		URL server_url = new URL(base_url + filename + Constants.COMPRESSED);
		File local_file = new File(filename);

		if (!local_file.exists()) {
			// invalidate etag & lastmodified as local file missing
			this.etag = null;
			this.lastmodified = null;
		}

		// obtain the connection
		System.setProperty("http.agent", Strings.getString("FullName"));
		// alternative to http.agent
		// sourceConnection.setRequestProperty("User-Agent", "paul"); //
		HttpURLConnection.setFollowRedirects(true);
		HttpURLConnection sourceConnection = (HttpURLConnection) server_url
				.openConnection();
		sourceConnection.setRequestProperty("Accept-Encoding", "gzip");

		if (etag != null) {
			sourceConnection.addRequestProperty("If-None-Match", etag);
		}

		if (lastmodified != null) {
			sourceConnection.addRequestProperty("If-Modified-Since",
					lastmodified);
		}

		// establish connection, get response headers
		sourceConnection.connect();
		response_code = sourceConnection.getResponseCode();

		switch (response_code) {
		case HttpURLConnection.HTTP_OK: {
			// get the last modified & etag and store them for the next check
			this.lastmodified = sourceConnection
					.getHeaderField("Last-Modified");
			this.etag = sourceConnection.getHeaderField("ETag");

			copy(sourceConnection.getInputStream(), local_file);

			break;
		}
		case HttpURLConnection.HTTP_NOT_MODIFIED: {
			// file not modified, we already have the content
			break;
		}
		default: {
			// bad response
			throw new ConnectException(filename + "=" + response_code + ":"
					+ sourceConnection.getResponseMessage());
		}
		}

		sourceConnection.disconnect();

		// wait 1 second (1000 milli-sec) as recommended by oztivo host
		// web-master
		Thread.sleep(1000);
	}

	/**
	 * Copies the compressed server file to a local file. If the destination
	 * file does not exist, it is created.
	 * 
	 * @param src
	 *            the source file
	 * @param dst
	 *            the destination file
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void copy(InputStream src, File dst) throws IOException {
		GZIPInputStream gzis = new GZIPInputStream(src);
		InputStreamReader xis = new InputStreamReader(gzis);
		BufferedReader is = new BufferedReader(xis);

		OutputStream out = new FileOutputStream(dst);
		OutputStreamWriter osw = new OutputStreamWriter(out);
		BufferedWriter os = new BufferedWriter(osw);

		String line;
		// Now read lines of text: the BufferedReader puts them in lines,
		// the InputStreamReader does Unicode conversion, and the
		// GZipInputStream "gnuzip"s the data from the FileInputStream.
		while ((line = is.readLine()) != null) {
			os.write(line);
			os.newLine();
		}

		os.flush();
		out.close();
		gzis.close();
	}

	/**
	 * Removes the local file.
	 */
	public void removeLocalFile() {
		File local_file = new File(filename);
		if (local_file.exists()) {
			local_file.delete();
		}

		// invalidate etag & lastmodified as local file has been removed
		this.etag = null;
		this.lastmodified = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerFile [filename=" + filename + ", datafor=" + datafor
				+ ", base_url=" + base_url + ", etag=" + etag
				+ ", lastmodified=" + lastmodified + ", response_code="
				+ response_code + "]";
	}

	/**
	 * Test stub main method
	 * 
	 * @param argv
	 *            the argv
	 */
	public static void main(String[] argv) {
		try {
			ServerFile sf = new ServerFile(Constants.CHANNEL, Utility
					.DateToString(Utility.today()), Constants.BASE_URL);
			sf.getGZFileFromServer();
			System.out.println(sf.toString());

			sf.getGZFileFromServer();
			System.out.println(sf.toString());
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
