package name.paulshipley.xmltv;

import java.util.Locale;

import javax.swing.UIManager;

import name.paulshipley.Common.About;
import name.paulshipley.Common.ExceptionHandler;
import name.paulshipley.Common.LogFiles;
import name.paulshipley.Common.Machine;

/**
 * Command line wrapper.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Main.java,v 1.5 2010/03/23 08:54:15 paul Exp $
 */
public class Main {
	/**
	 * Instantiates a new main.
	 */
	public Main() {
		super();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			LogFiles logs = LogFiles.getInstance();
			logs.setErrFile("xmltvErr.txt");
			logs.setOutFile("xmltvLog.txt");

			Messages Message = new Messages(Locale.ENGLISH);
			ExceptionHandler.setTitle(Message.getString("Title"));

			if (Machine.isMacOS()) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty(
						"com.apple.mrj.application.apple.menu.about.name",
						Strings.getString("Name"));
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}

			About about = new About(Main.class.getResourceAsStream("About.txt"));
			System.err.println(about.toString());
			System.out.println(about.toString());
			System.out.println(Machine.getInstance().list());

			ChannelManager cm = ChannelManager.instance();
			cm.run();

			logs.flushOut();
			logs.flushErr();
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
