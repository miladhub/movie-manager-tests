package org.sample.movie.ft;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class ScannerDarklyTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://localhost:8080/", "*chrome");
	}
	public void testNew() throws Exception {
		selenium.open("/Movie/");
		selenium.type("searchForm:searchInput", "a scanner darkly");
		selenium.click("searchForm:searchCommand");
		selenium.waitForPageToLoad("30000");
	}
}
