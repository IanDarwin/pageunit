package pageunit.html;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;

/** Test handling of META redirects */
public class ParserTest5 extends TestCase {
	
	final static String testData = "<html><head><title>DuMmY</title></head>" + 
		"<body><table>" +
		"<tr>" +
		"<td 1409='' onclick='document.location='http://lims.phenogenomics.ca/cryo/embryoCryoHeader.jsp?NavBarId=" +
		"sub28';' onmouseover='event.cancelBubble=true;document.getElementById('sub28navbar1').className='navbar1" +
		"HOVERSTYLE';' onmouseout='this.className='navbar1CELLBGSTYLE';event.cancelBubble=true;document.getElemen" +
		"tById('sub28navbar1').className='navbar1HREFSTYLE';' class='navbar1CELLBGSTYLE' nowrap='nowrap'>" +
		"<a name='subMenu8sub28navbar1' id='subMenu8sub28navbar1' onclick='return false;' href='http://lims.pheno" +
		"genomics.ca/cryo/embryoCryoHeader.jsp?NavBarId=sub28' onmouseout='fncnavbar1_MarkerImage(document.Marker" +
		"sub28navbar1);" +
		"document.getElementById('sub28navbar1').className='navbar1HREFSTYLE';' class='navbar1HREFSTYLE'>" +
		"</a><table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
		"<tbody><tr><td width='5%'><img src='admin.index.jsp_files/itemmarker.gif' name='Markersub28navbar1' onlo" +
		"ad='navbar1_imageLoaded();' border='0' hspace='0'><img src='admin.index.jsp_files/navpad.gif' border='0'" +
		" hspace='3'>" +
		"</td>" +
		"<td nowrap='nowrap' width='95%'><a name='sub28navbar1' id='sub28navbar1' onmouseover='popupview8navbar1S" +
		"howPopup();' onmouseout='fncnavbar1_MarkerImage(document.Markersub28navbar1);popupview8navbar1Popup();" +
		"document.getElementById('sub28navbar1').className='navbar1HREFSTYLE';' onclick='return false;' href='htt" +
		"p://lims.phenogenomics.ca/cryo/embryoCryoHeader.jsp?NavBarId=sub28' class='navbar1HREFSTYLE'>Embryo Cyro" +
		"preservation</a>" +
		"</td>" +
		"</tr>" +
		"</tbody></table>" +
		"</td>" +
		"</tr></tbody></table></div>" +
		"<script language='JavaScript1.2'>" +
		" function  popupsubMenu8navbar1getPixelTop(htmlElementTop)" +
		" {" +
		"  yPos = htmlElementTop.offsetTop;" +
		"  tempEl = htmlElementTop.offsetParent;" +
		"  while (tempEl != null) {" +
		"                        yPos += tempEl.offsetTop;" +
		"                        tempEl = tempEl.offsetParent;" +
		"         }" +
		"  return yPos;" +
		" }" +
		"</script></body></html>\n";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		// System.err.println("Analyze THIS: " + testData);
		page = new HTMLParser().parse(new StringReader(testData));
		assertNotNull("parse", page);
	}
	
	public void test1() {
		
	}
}
