package regress.linkchecker;

import java.net.URL;

import pageunit.linkchecker.LinkChecker;
import junit.framework.TestCase;

public class LinkCheckerTest extends TestCase {
	
	public void testSingleURL() throws Exception {
		System.out.println(LinkChecker.checkOneLink(new URL("http://www.sun.com/")));
	}
	
	public void testSite() throws Exception {
		System.out.println("LinkCheckerTest.testSite() starting");
		LinkChecker.checkStartingAt(new URL("http://www.phenogenomics.ca/"));
		System.out.println("LinkCheckerTest.testSite() done");

	}
}
