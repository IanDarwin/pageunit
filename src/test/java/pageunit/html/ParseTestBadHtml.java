package pageunit.html;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/** Test handling really bad HTML */
public class ParseTestBadHtml extends TestCase {
	
	final static String twoHTMLtagsData = "<html><head><title>DuMmY</title></head>" + 
		"<body><html><head></head><body><p>Hello</p>" +
		"</script></body></html>\n";
	
	private HTMLPage page;
	
	public void test1() throws IOException, HTMLParseException {
		// System.err.println("Analyze THIS: " + testData);
		page = new HTMLParser().parse(new StringReader(twoHTMLtagsData));
		assertNotNull("parse", page);
	}
}
