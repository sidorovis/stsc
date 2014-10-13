package stsc.frontend.zozka.settings;

import stsc.frontend.zozka.gui.models.TextAlgorithmParameter;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TextAlgorithmParameterTest extends TestCase {

	public void testTextAlgorithmParameterTestDomen() {
		Assert.assertEquals(3, TextAlgorithmParameter.createDomenRepresentation("'','',''").size());
		Assert.assertEquals("", TextAlgorithmParameter.createDomenRepresentation("'','',''").get(0));
		Assert.assertEquals("", TextAlgorithmParameter.createDomenRepresentation("'','',''").get(1));
		Assert.assertEquals("", TextAlgorithmParameter.createDomenRepresentation("'','',''").get(2));

		Assert.assertEquals("hello", TextAlgorithmParameter.createDomenRepresentation("'','hello',''").get(1));
		Assert.assertEquals("vikal", TextAlgorithmParameter.createDomenRepresentation("'','hello','vikal'").get(2));
	}
}
