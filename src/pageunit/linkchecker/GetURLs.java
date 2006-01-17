package pageunit.linkchecker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class GetURLs {
	
	/** The tag reader */
	ReadTag reader;

	public GetURLs(URL theURL) throws IOException {
		reader = new ReadTag(theURL);
	}

	public GetURLs(String theURL) throws MalformedURLException, IOException {
		reader = new ReadTag(theURL);
	}

	/* The tags we want to look at */
	public final static String[] wantTags = {
		"a",
		"applet",
		"img",
		"frame",
	};

	public void close() throws IOException {
		if (reader != null) 
			reader.close();
	}
	public static void main(String[] argv) throws 
			MalformedURLException, IOException {
		String theURL = argv.length == 0 ?
			"http://localhost/" : argv[0];
		GetURLs gu = new GetURLs(theURL);
		gu.reader.setWantedTags(GetURLs.wantTags);
		List urls = gu.reader.readTags();
		Iterator urlIterator = urls.iterator();
		while (urlIterator.hasNext()) {
			System.out.println(urlIterator.next());
		}
	}
}
