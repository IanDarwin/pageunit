package pageunit;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * WebTest filter that checks every page for $Source or $Id
 * @version $Id$
 */
public class CvsIdFilter implements TestFilter {

	/* (non-Javadoc)
	 * @see regress.webtest.TestFilter#filterPage(com.gargoylesoftware.htmlunit.html.HtmlPage, com.gargoylesoftware.htmlunit.WebResponse)
	 */
	public void filterPage(HtmlPage thePage, WebResponse theResult) throws Exception {

		String contentAsString = theResult.getContentAsString();
		if (!(contentAsString.indexOf("$Source") > -1) && !(contentAsString.indexOf("$Id") > -1))  {
			throw new RuntimeException("ERROR: Page does not have a CVS Identifier (neither Source nor Id)");
		}
	}

}
