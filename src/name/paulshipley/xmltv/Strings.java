package name.paulshipley.xmltv;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The Class Strings.
 * Externalize String definitions
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Strings.java,v 1.3 2009/12/30 01:22:36 paul Exp $
 */
public class Strings {
	private static final String BUNDLE_NAME = "name.paulshipley.xmltv.strings"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Instantiates a new strings bundle.
	 */
	private Strings() {
	}

	/**
	 * Gets the string from the definitions in strings.properties
	 * 
	 * @param key
	 *            the key name of the definition
	 * 
	 * @return the string
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
