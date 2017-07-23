package name.paulshipley.xmltv;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import name.paulshipley.Common.About;
import name.paulshipley.Common.ExceptionHandler;
import name.paulshipley.Common.PositionComponent;
import name.paulshipley.Common.Progress;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Class ChannelManager <br/>
 * 
 * Manages the list of Channel objects that represent each of the channels that
 * can be selected for processing.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ChannelManager.java,v 1.10 2010/03/29 11:56:25 paul Exp $
 */
public class ChannelManager {
	private static ChannelManager instance = new ChannelManager();

	private final File Cfile = new File(Constants.CHANNEL);
	private final File Sfile = new File(Constants.SELECTED);
	private ChannelList Channellist;

	private ServerFileManager sfm = ServerFileManager.instance();
	private String actionCmd = "";

	private Messages Message = new Messages(Locale.ENGLISH);

	private ChannelManagerVisual cmv;

	/**
	 * Instantiate this Singleton.
	 */
	private ChannelManager() {
		super();

		try {
			init();
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}
	}

	/**
	 * Get this Instance (Singleton).
	 * 
	 * @return the server file manager instance
	 */
	public static ChannelManager instance() {
		return instance;
	}

	/**
	 * Initialise this class.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws ParseException 
	 */
	private void init() throws IOException, InterruptedException,
			ParserConfigurationException, TransformerException, ParseException {
		Channellist = new ChannelList();

		// get channels xml file from server
		// use null for datefor value as channel files do not expire
		sfm.add(new ServerFile(Constants.CHANNEL, null, Constants.BASE_URL));

		// if not exists create empty selected channels xml file
		if (!this.Sfile.exists()) {
			this.save();
		}
	}

	/**
	 * Load XML Channel list into this manager and flag selected channels.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 *             the SAX exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	private void load() throws IOException, ParserConfigurationException,
			SAXException {
		// validate configuration
		assert this.Cfile != null : "channel file is null";
		assert this.Sfile != null : "selected file is null";
		assert this.Channellist != null : "list is null";

		// load list from channels xml
		ChannelParser cp = new ChannelParser(this.Cfile, this.Channellist);
		cp.parse();

		// update list with selected channels
		SelectedChannelParser scp = new SelectedChannelParser(this.Sfile);
		ArrayList<String> sl = scp.getSelectedList();

		Iterator<String> channelit = sl.iterator();
		while (channelit.hasNext()) {
			String channel = channelit.next();
			update_channel(channel);
		}
	}

	/**
	 * Update channel list with selected channel.
	 * 
	 * @param channel
	 *            the channel that is selected
	 */
	private void update_channel(String channel) {
		assert this.Channellist != null : "Channellist is null";

		Iterator<Channel> channelit = this.Channellist.iterator();
		while (channelit.hasNext()) {
			Channel ch = channelit.next();
			if (ch.getId().equals(channel)) {
				ch.setSelected(true);
				// Debug.println(ch.toString());
			}
		}
	}

	/**
	 * Show dialog to select channels to process.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void showDialog() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		cmv = new ChannelManagerVisual();

		cmv.setTitle(Message.getString("Title"));
		cmv.getJMenu().setText(Strings.getString("Name"));

		// configure window
		cmv.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				synchronized (instance) {
					instance.notifyAll();
				}
			}
		});

		// centre frame on screen
		PositionComponent pos = new PositionComponent(cmv);
		pos.Centre();
		cmv.setLocation(pos.getComponentLocation());

		// configure buttons
		cmv.getJButton1().setActionCommand(Constants.SAVE);
		cmv.getJButton1().setText(Message.getString(Constants.SAVE));
		cmv.getJButton1().addActionListener(new cmActionListener());

		cmv.getJButton2().setActionCommand(Constants.PROCESS);
		cmv.getJButton2().setText(Message.getString(Constants.PROCESS));
		cmv.getJButton2().addActionListener(new cmActionListener());

		cmv.getJButton3().setActionCommand(Constants.QUIT);
		cmv.getJButton3().setText(Message.getString(Constants.QUIT));
		cmv.getJButton3().addActionListener(new cmActionListener());

		// configure menus
		cmv.getJMenuItem1().setText(Message.getString(Constants.ABOUT));
		cmv.getJMenuItem1().addActionListener(new aboutActionListener());

		cmv.getJMenuItem2().setActionCommand(Constants.QUIT);
		cmv.getJMenuItem2().setText(Message.getString(Constants.QUIT));
		cmv.getJMenuItem2().addActionListener(new cmActionListener());

		// add channels to the scroll pane
		JPanel p = cmv.getJPanel();

		Iterator<Channel> chit = this.Channellist.iterator();
		while (chit.hasNext()) {
			Channel ch = chit.next();
			// Debug.println(ch.toString());
			p.add(ch.display());
		}

		// show GUI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				cmv.setVisible(true);
				cmv.repaint();
			}
		});

		// wait for GUI thread to terminate
		synchronized (instance) {
			try {
				instance.wait();
			} catch (InterruptedException e) {
				System.out.println("Interrupted..." + e.getMessage());
			}
		}

		// hide GUI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				cmv.setVisible(false);
				cmv.dispose();
			}
		});
	}

	/**
	 * The listener interface for receiving Action events. The class that is
	 * interested in processing a Action event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addActionListener<code> method. When
	 * the Action event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see ActionEvent
	 */
	private class cmActionListener implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent event) {
			actionCmd = event.getActionCommand();
			synchronized (instance) {
				instance.notifyAll();
			}
		}
	}

	/**
	 * This listener displays the About dialog in a separate thread.
	 * 
	 * @see ActionEvent
	 */
	private class aboutActionListener implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent event) {
			// display the About dialog in separate thread
			Thread aet = new Thread(new Runnable() {
				public void run() {
					try {
						About about = new About();
						about.setMessage(Main.class
								.getResourceAsStream("About.txt"));
						about.display();

					} catch (Exception e) {
						ExceptionHandler.handleAndTerminate(e);
					}
				}
			});
			aet.start();
		}
	}

	/**
	 * Persist the selected channel list.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	private void save() throws IOException, ParserConfigurationException,
			TransformerException {
		// validate configuration
		assert this.Sfile != null : "selected file is null";
		assert this.Channellist != null : "list is null";

		// save list to xml
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder parser = dbf.newDocumentBuilder();
		parser.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException e) {
				ExceptionHandler.handleAndTerminate(e);
			}

			public void error(SAXParseException e) {
				ExceptionHandler.handleAndTerminate(e);
			}

			public void fatalError(SAXParseException e) {
				ExceptionHandler.handleAndTerminate(e);
			}
		});

		Document document = parser.newDocument();

		// create document root <tv generator-info-name="Wktivoguide v5">
		Element root = document.createElement("tv");
		root.setAttribute("generator-info-name", Strings.getString("FullName"));

		// append the selected channels like <channel id="NineHD" />
		Iterator<Channel> chlit = this.Channellist.iterator();
		while (chlit.hasNext()) {
			Channel ch = chlit.next();
			if (ch.getSelected()) {
				Element selected = document.createElement("channel");
				selected.setAttribute("id", ch.getId());
				root.appendChild(selected);
			}
		}

		// build xml
		document.appendChild(root);

		// output xml to stream
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer tr = tf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(new DOMSource(document), new StreamResult(this.Sfile));
	}

	/**
	 * Process the list of selected channels, generating a xmltv file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private void process() throws IOException, TransformerException,
			ParserConfigurationException, SAXException, InterruptedException {
		// create GUI to show progress of DataListProcessor
		final Progress p = new Progress("Processing...");
		p.setTitle(Message.getString("Title"));
		p.setNote("");

		// run DataListProcessor in separate thread
		Thread pt = new Thread(new Runnable() {
			public void run() {
				try {
					DatalistProcessor dlp = new DatalistProcessor();
					dlp.addObserver(new Observer() {
						public void update(Observable obj, Object arg) {
							// update progress GUI as DataListProcessor reports
							p.setNote((String) arg);
						}
					});

					// generate xmltv file
					dlp.generate();

					// notify main thread when finished
					synchronized (instance) {
						instance.notifyAll();
					}
				} catch (Exception e) {
					ExceptionHandler.handleAndTerminate(e);
				}
			}
		});
		pt.start();

		// wait for processing thread to terminate
		synchronized (instance) {
			try {
				instance.wait();
			} catch (InterruptedException e) {
				System.out.println("Interrupted..." + e.getMessage());
			}
		}

		// close progress GUI
		p.done();
	}

	/**
	 * Run the ChannelManager.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public void run() throws IOException, ParserConfigurationException,
			SAXException, TransformerException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		System.out.println("Load");
		load();

		System.out.println("Show Dialog");
		showDialog();

		if (actionCmd.equals(Constants.SAVE)) {
			System.out.println(Constants.SAVE);
			save();
			JOptionPane.showMessageDialog(null, Message
					.getString("Success_Save"), Message.getString("Title"),
					JOptionPane.INFORMATION_MESSAGE, null);
		} else if (actionCmd.equals(Constants.PROCESS)) {
			System.out.println(Constants.PROCESS);
			save();
			process();
			JOptionPane.showMessageDialog(null, Message
					.getString("Success_Process"), Message.getString("Title"),
					JOptionPane.INFORMATION_MESSAGE, null);
		} else if (actionCmd.equals(Constants.QUIT)) {
			System.out.println(Constants.QUIT);
		}

		System.out.println("exit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelManager [Cfile=" + Cfile + ", Channellist="
				+ Channellist + ", sfm=" + sfm + "]";
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the argv
	 */
	public static void main(String[] argv) {
		try {
			ChannelManager cm = ChannelManager.instance();
			cm.run();
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
