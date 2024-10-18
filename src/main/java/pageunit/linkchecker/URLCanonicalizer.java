package pageunit.linkchecker;

import java.net.URI;
import java.net.URISyntaxException;

public class URLCanonicalizer {
    public static String getCanonicalURL(String urlString) {
        try {
            URI uri = new URI(urlString);
            String canonicalUrl = uri.normalize().toString();
            return canonicalUrl;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
