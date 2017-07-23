package name.paulshipley.xmltv;

import java.util.Enumeration;
import java.util.Locale;

import name.paulshipley.Common.Resources;

/**
 * Wrapper for strings contained in ResourceBundle Messages.properties.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Messages.java,v 1.2 2009/12/22 11:59:24 paul Exp $
 */
public class Messages extends Resources {
	/**
	 * Instantiates a new messages class.
	 */
	public Messages() {
		super();
	}

	/**
	 * Instantiates a new messages class.
	 * 
	 * @param country the country
	 */
	public Messages(Locale country) {
		super(country);
	}

	/**
	 * Test stub main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Locale country = Locale.ENGLISH;
		Messages Message = new Messages(country);
		System.out.println(Message.getClass().getName());
		System.out.println(Message.getString("Next_>"));

		Enumeration<String> keys = Message.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			System.out.println(key + "=" + Message.getString(key));
		}
	}
}
