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
	
	String htmlText = "<html><head><foo><bar></head><body><a href='http://grelber/' name=grelber>";
	
	public void testReadAll() throws Exception {
		Reader is = new StringReader(htmlText);
		ReadTag red = new ReadTag(is);
		List readTags = red.readTags();
		assertNotNull("list from readTags", readTags);
		assertTrue("any tags from readTags", 6 == readTags.size());
		Iterator tagsIterator = readTags.iterator();
		while (tagsIterator.hasNext()) {
			System.out.println(tagsIterator.next());
		}
	}
	
	public void testReadSome() throws Exception {
		Reader is = new StringReader(htmlText);
		ReadTag red = new ReadTag(is);
		red.setWantedTags(new String[] { "a", "foo" });
		List readTags = red.readTags();
		assertNotNull("list from readTags", readTags);
		assertTrue("any tags from readTags", 2 == readTags.size());
		Iterator tagsIterator = readTags.iterator();
		while (tagsIterator.hasNext()) {
			System.out.println(tagsIterator.next());
		}
	}
	
	public void testReadNone() throws Exception {
		Reader is = new StringReader(htmlText);
		ReadTag red = new ReadTag(is);
		red.setWantedTags(new String[] { });
		List readTags = red.readTags();
		assertNotNull("list from readTags", readTags);
		assertTrue("any tags from readTags", 0 == readTags.size());
		Iterator tagsIterator = readTags.iterator();
		while (tagsIterator.hasNext()) {
			System.out.println(tagsIterator.next());
		}
	}
}
