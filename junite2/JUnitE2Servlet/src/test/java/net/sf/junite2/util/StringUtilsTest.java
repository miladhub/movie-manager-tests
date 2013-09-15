/**
 * 
 */
package net.sf.junite2.util;

import static org.junit.Assert.*;

import net.sf.junite2.util.StringUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Martin
 */
public class StringUtilsTest{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception{}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception{}

	/**
	 * Test method for {@link net.sf.junite2.util.StringUtils#htmlText(java.lang.String)}.
	 */
	@Test
	public void testHtmlText(){
		String ostr = StringUtils.htmlText("<");
		assertEquals("Convert of < failed", "&lt;", ostr);
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.junite2.util.StringUtils#xmlText(java.lang.String)}.
	 */
	@Test
	public void testXmlText(){
	//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.junite2.util.StringUtils#filterStack(java.lang.String)}.
	 */
	@Test
	public void testFilterStack(){
	//fail("Not yet implemented");
	}

}
