package name.paulshipley.xmltv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Utility Test Cases.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: UtilityTest.java,v 1.1 2010/01/04 09:28:55 paul Exp $
 */
public class UtilityTest {
	Date today;       // current date
	Date tomorrow;    // today +1
	String today_str; // today as a string

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
		today = cal.getTime();
		cal.add(Calendar.DATE, 1);
		tomorrow = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		today_str = df.format(today);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link name.paulshipley.xmltv.Utility#today()}.
	 * @throws ParseException 
	 */
	@Test
	public final void testToday() throws ParseException {
		assertEquals("Same date", Utility.today(), today);
	}

	/**
	 * Test method for {@link name.paulshipley.xmltv.Utility#isFutureDate(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public final void testIsFutureDate() throws ParseException {
		assertTrue("Tomorrow is future", Utility.isFutureDate(tomorrow));
	}

	/**
	 * Test method for {@link name.paulshipley.xmltv.Utility#StringToDate(java.lang.String)}.
	 * @throws ParseException 
	 */
	@Test
	public final void testStringToDate() throws ParseException {
		assertEquals("Parsed same", Utility.StringToDate(today_str), today);
	}

}
