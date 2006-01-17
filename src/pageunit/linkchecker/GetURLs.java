package pageunit.linkchecker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetURLs {
	
	/** The tag reader */
	private ReadTag reader;

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

	public ReadTag getReader() {
		return reader;
	}
}
