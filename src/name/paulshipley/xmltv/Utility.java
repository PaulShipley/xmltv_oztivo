package name.paulshipley.xmltv;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import name.paulshipley.Common.Debug;
import name.paulshipley.Common.ExceptionHandler;

/**
 * Utilities.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Utility.java,v 1.6 2010/03/23 08:54:15 paul Exp $
 */
public class Utility {

	/**
	 * Today.<br/>
	 * Returns the current date, normally the system date but during debuging
	 * will be overridden.
	 * 
	 * @return the date
	 * @throws ParseException
	 */
	public static Date today() throws ParseException {
		Date today;

		if (Debug.DEBUG) {
			today = Utility.StringToDate("2009-11-22");
		} else {
			Calendar cal = Calendar.getInstance();
			today = Utility.removeTime(cal.getTime());
		}

		return today;
	}

	/**
	 * Checks if parameter a future date (including today).
	 * 
	 * @param date_value
	 *            the date_value
	 * 
	 * @return the boolean
	 * @throws ParseException
	 */
	public static Boolean isFutureDate(Date date_value) throws ParseException {
		boolean future = false;

		Calendar cal = Calendar.getInstance();
		cal.setTime(Utility.today());
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();

		if (date_value.after(yesterday)) {
			// future including today
			future = true;
		}

		return future;
	}

	/**
	 * Parses a String value in "yyyy-MM-dd" format into a Date.
	 * 
	 * @param date_string
	 *            the date string
	 * 
	 * @return the date
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date StringToDate(String date_string) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date_value = null;

		date_value = df.parse(date_string);

		return date_value;
	}

	/**
	 * Formats a Date into a String value in "yyyy-MM-dd" format.
	 * 
	 * @param date the date
	 * 
	 * @return the string
	 */
	public static String DateToString(Date date)  {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date_string = null;

		date_string = df.format(date);

		return date_string;
	}

	/**
	 * Removes the time components from a Date object.
	 * 
	 * @param date
	 *            the date
	 * 
	 * @return the date (without time components)
	 */
	public static Date removeTime(Date date) {
		Calendar cal = Calendar.getInstance();

		// Make sure the calendar will not perform automatic correction.
		cal.setLenient(false);

		// Set the time of the calendar to the given date.
		cal.setTime(date);

		// Remove the time.
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// Return the date again.
		return cal.getTime();
	}

	/**
	 * Test stub main method.
	 * 
	 * @param argv
	 *            the command line arguments
	 */
	public static void main(String[] argv) {
		try {
			Date today = Utility.today();
			Date parsed = Utility.StringToDate("2009-11-26");
			Boolean future = Utility.isFutureDate(parsed);

			Debug.println("Debug mode");
			System.out.println("Today is " + today.toString());
			System.out.println("Parsed date is " + parsed.toString());
			System.out.println("Date is in the future? " + future.toString());
		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
