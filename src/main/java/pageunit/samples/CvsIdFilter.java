package pageunit.samples;

import pageunit.TestFilter;

import pageunit.http.WebResponse;
import pageunit.html.HTMLPage;

/**
 * WebTest filter that checks every page for $Source or $Id
 * @version $Id$
 */
public class CvsIdFilter implements TestFilter {

	/* (non-Javadoc)
	 * @see pageunit.TestFilter#filterPage(com.gargoylesoftware.htmlunit.html.HtmlPage, com.gargoylesoftware.htmlunit.WebResponse)
	 */
	public void filterPage(HTMLPage thePage, WebResponse theResult) throws Exception {

		String contentAsString = theResult.getContentAsString();
		if (!(contentAsString.indexOf("$Id") > -1) && 
			!(contentAsString.indexOf("$Source") > -1) &&
			!(contentAsString.indexOf("$Version") > -1))  {
			throw new RuntimeException("ERROR: Page does not have any valid CVS Identifier");
		}
	}

}
