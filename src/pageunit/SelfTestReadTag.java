package regress.webtest;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test the Tag Reader
 * 
 * @version $Id$
 */
public class SelfTestReadTag extends TestCase {
	
	public void testFoo() throws Exception {
		String theURL = "<html><head><foo><bar></head><body><a href='http://grelber/' name=grelber>";
		Reader is = new StringReader(theURL);
		ReadTag red = new ReadTag(is);
		List readTags = red.readTags();
		assertNotNull("list from readTags", readTags);
		assertFalse("any tags from readTags", 0 == readTags.size());
		Iterator tagsIterator = readTags.iterator();
		while (tagsIterator.hasNext()) {
			System.out.println(tagsIterator.next());
		}
	}
}
