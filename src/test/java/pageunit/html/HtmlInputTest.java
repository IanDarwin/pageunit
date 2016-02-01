package pageunit.html;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HtmlInputTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConstructor() {
		new HTMLInputImpl("test1", "text");
		assertSame(new HTMLInputImpl("test2", "image").getInputType(), HTMLInputType.IMAGE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorBad() {
		new HTMLInputImpl("test0", "nonsense");
	}
}
