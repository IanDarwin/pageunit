package regress.linkchecker;

import java.net.URL;

import junit.framework.TestCase;
import pageunit.linkchecker.LinkChecker;

public class LinkCheckerTest extends TestCase {
	
	public void testSingleURL() throws Exception {
		System.out.println(LinkChecker.checkOneLink(new URL("http://www.sun.com/")));
	}
	
	public void testSite() throws Exception {
		System.out.println("LinkCheckerTest.testSite() starting");
		URL rootURL = new URL("http://www.darwinsys.com/file/");
		
		LinkChecker.checkStartingAt(rootURL.toString());

		System.out.println("LinkCheckerTest.testSite() done");

	}
}
