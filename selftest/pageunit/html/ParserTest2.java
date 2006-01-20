package regress.html;

import java.io.IOException;
import java.io.StringReader;

import pageunit.html.*;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;
import junit.framework.TestCase;

public class ParserTest2 extends TestCase {
	/** This is pretty much a "wild type" page, taken from
	 * calendar.phenogenomics.ca and only cleaned up minorly
	 * (and quotes escaped to make it compile as a String).
	 */
	String testData = 
	"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" +
	"<html lang=\"en\" >" +
	"<head><title>This Title Added</title>" +
	"<link rel=stylesheet type='text/css' href='./layout/default/css/common.css'>" +
	"<style type=\"text/css\"><link type='text/css' " +
		"rel=stylesheet href=\"./layout/default/css/common.css\"></style></style>" +
		"<script type=\"text/javascript\"  src=\"./lib/chkform.js\"></script>" +
		"</head>" +
		"<body bgcolor=\"#E0E0E0\" onLoad=\"document.frm.loginstring.focus();\">" +
		"<center>" +
		"<form action='index.php' method='post' name='frm' " +
		"onSubmit=\"return chkForm('frm','loginstring','Invalid name/password\">" +
		"<input type='hidden' name='loginform' value='1'>" +
		"<table cellspacing=1 cellpadding=5 bgcolor=#D5D5D5 border=0>" +
		"tr><td>&nbsp;</td><td><br><br><img src='./img/logo.gif' border='0'></td></tr>" +
		"<tr><td>Login</td><td><input type='text' name='loginstring'></td></tr>" +
		"<tr><td>Password</td><td><input type='password' name='user_pw'></td></tr>" +
		"<tr><td>&nbsp;</td><td><input type='submit' value='go'></td></tr>" +
		"<tr><td align='left' colspan=2>&nbsp;&nbsp;&nbsp;&nbsp;</td></tr></table>" +
		"</form></center></body></html>";

	public void testAll() throws IOException, HTMLParseException {
		HTMLPage page = new HTMLParser().parse(new StringReader(testData));
		assertNotNull("parse result", page);
		HTMLForm form = page.getFormByName("frm");
		assertNotNull(form);
		HTMLInput username = form.getInputByName("loginstring");
		assertNotNull("get name field", username);
		HTMLInput passwd = form.getInputByName("user_pw");
		assertNotNull("get pw field", passwd);
		assertNotNull(page.getTitleText());
	}
}
