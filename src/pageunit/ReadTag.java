package regress.webtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** A simple but reusable recursive-descent HTML/XML tag extractor.
 * @author Ian Darwin, Darwin Open Systems, www.darwinsys.com.
 * @version $Id$
 */
public class ReadTag {

	/** The URL that this ReadTag object is reading */
	protected String myOrigin = null;
	/** The Reader for this object */
	protected BufferedReader inrdr = null;
	/** The set of tags we want to look for; null to return all tags */
	protected String[] wantedTags;
	
	public static final char XML_TAG_START = '<';
	private static final char XML_TAG_END = '>';
	private static final String XML_ENDTAG_LEADIN = "/";

	/** Construct a ReadTag given a URL String */
	public ReadTag(String theURLString) throws 
			IOException, MalformedURLException {

		this(new URL(theURLString));
	}

	/** Construct a ReadTag given a URL */
	public ReadTag(URL theURL) throws IOException {
		myOrigin = theURL.toString();
		// Open the URL for reading
		inrdr = new BufferedReader(new InputStreamReader(theURL.openStream()));
	}
	
	public ReadTag(Reader rdr) {
		myOrigin = "(pre-opened reader)";
		inrdr = new BufferedReader(rdr);
	}
	
	public ReadTag(InputStream is) {
		this(new InputStreamReader(is));
	}
	
	/**
	 * Allows you to specify a list of tags; only used if you call readTags(), not nextTag().
	 * @param tags The tags to set.
	 */
	public void setWantedTags(String[] tags) {
		this.wantedTags = tags;
	}
	
	public List readTags() throws IOException {
		List tags = new ArrayList();
		Element aTag;
		while ((aTag = nextTag()) != null) {
			if (aTag.getType().startsWith(XML_ENDTAG_LEADIN))
				continue;
			if (wantedTags == null) {
				tags.add(aTag);
			} else {
				for (int i = 0; i < wantedTags.length; i++) {
					if (aTag.getType().equals(wantedTags[i])) {
						tags.add(aTag);
						break;
					}
				}
			}
		}
		return tags;
	}
	
	/** Read the next tag.  */
	protected Element nextTag() throws IOException {
		int i;
		while ((i = inrdr.read()) != -1) {
			char thisChar = (char)i;
			if (thisChar == XML_TAG_START) {
				Element tag = readTag();
				return tag;
			}
		}
		return null; // at EOF
	}

	/** Read one tag. 
	 * @author Ian Darwin
	 * @author Long ago it was based on code that I adapted from code by Elliotte Rusty Harold
	 */
	protected Element readTag() throws IOException {
		StringBuffer tagType = new StringBuffer(XML_TAG_START);
		int i = XML_TAG_START;
	  
		while ((i = inrdr.read()) != -1 && i != XML_TAG_END && 
				
				!Character.isWhitespace((char)i)) {
			
				tagType.append((char)i);
		}    

		Element tag = new Element(tagType.toString());
		if (i == XML_TAG_END) {
			return tag;		// not attributes
		}
		readAttributes(tag, i);
		return tag;
	}

	/** Read all the attributes for the current tag.
	 * @param tag
	 * @throws IOException
	 */
	private void readAttributes(Element tag, int i) throws IOException {
		final int S_INNAME = -1, S_EQUALS = '=', Q_NONE = 'N', Q_SQUOTE = '\'', Q_DQUOTE = '"';
		final int S_INITIAL = S_INNAME;
		int state = S_INNAME;
		StringBuffer attrName = new StringBuffer(), attrValue = new StringBuffer();
		while (i != XML_TAG_END && (i = inrdr.read()) != -1) {
			
			if (state == Q_SQUOTE && i != Q_SQUOTE) {
				attrValue.append((char)i);
			} else if (state == Q_DQUOTE && i != Q_DQUOTE) {
				attrValue.append((char)i);
			} else if (i == '=') {
				state = S_EQUALS;
			} else if (i == Q_SQUOTE) {
				if (state == Q_SQUOTE) {// End of quoted string
					setOneAttribute(tag, attrName, attrValue);
					state = S_INITIAL;
				} else 
					state = Q_SQUOTE;
			} else if (i == Q_DQUOTE) {
				if (state == Q_DQUOTE) {// End of quoted string
					setOneAttribute(tag, attrName, attrValue);
					state = S_INITIAL;
				} else 
					state = Q_DQUOTE;
			} else if (Character.isWhitespace((char)i)) {
				setOneAttribute(tag, attrName, attrValue);
				state = S_INITIAL;
			} else {
				StringBuffer whereToPutChars = state==S_INNAME ? attrName : attrValue;
				whereToPutChars.append((char)i);
			}
		}
		if (attrName.length() > 0) {
			setOneAttribute(tag, attrName, attrValue);
		}
	}

	/**
	 * @param tag
	 * @param attrName
	 * @param attrValue
	 */
	private void setOneAttribute(Element tag, StringBuffer attrName, StringBuffer attrValue) {
		tag.setAttribute(attrName.toString(), attrValue.toString());
		attrName.setLength(0);
		attrValue.setLength(0);
	}

	public void close() throws IOException {
		inrdr.close();
	}


	/* Return a String representation of this object */
	public String toString() {
		return "ReadTag[" + myOrigin.toString() + "]";
	}
}
