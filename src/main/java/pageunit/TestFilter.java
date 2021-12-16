package pageunit;

import pageunit.html.HTMLPage;
import pageunit.http.WebResponse;

/**
 * Site-specific extension mechanism for WebTest package
 * @version $Id$
 */
public interface TestFilter {

		public void filterPage(HTMLPage thePage, WebResponse theResult) throws Exception;
}
