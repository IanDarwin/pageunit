package pageunit.html;

import junit.framework.TestCase;

public class ParserComponentStackTest extends TestCase {
	
	public void test() throws Exception {
		HTMLParser p = new HTMLParser();
		
		p.pushComponent(new HTMLHTMLImpl("MyHTMLImpl"));
		                                  
		p.pushComponent(new GenericHTMLContainer(null, "head"));
		                                  
		p.pushComponent(new GenericHTMLContainer(null, "body"));
	}
}
