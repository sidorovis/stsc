package stsc.algorithms;

import junit.framework.TestCase;

public class AlgorithmSettingsTest extends TestCase {
	public void testAlgorithmsSettings() {
		AlgorithmSettings as = new AlgorithmSettings();
		Double d = as.get("a");
		assertNull(d);
		assertNotNull(as.set("a", new Double(14.05)));
		assertNotNull(as.set("b", 14.05));
		assertEquals(as.get("a"), as.get("b"));
	}
}
