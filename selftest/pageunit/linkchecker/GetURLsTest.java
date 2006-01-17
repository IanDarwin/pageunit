package regress.linkchecker;

import java.util.List;

import junit.framework.TestCase;
import pageunit.linkchecker.Element;
import pageunit.linkchecker.GetURLs;

public class GetURLsTest extends TestCase {
	public void testOne() throws Exception {
		String theURL = "http://localhost/";
		GetURLs gu = new GetURLs(theURL);
		gu.getReader().setWantedTags(GetURLs.wantTags);
		List<Element> urls = gu.getReader().readTags();
		for (Element e : urls) {
			System.out.println(e);
		}
	}
}
