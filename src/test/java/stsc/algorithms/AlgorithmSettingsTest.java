package stsc.algorithms;

import junit.framework.TestCase;

public class AlgorithmSettingsTest extends TestCase {

	public void testAlgorithmsSettings() {
		final AlgorithmSettings as = new AlgorithmSettings();
		assertNull(as.get("a"));
		assertNotNull(as.set("a", new Double(14.05)));
		assertNotNull(as.set("b", 14.05));

		assertEquals(Double.valueOf(as.get("b")), Double.valueOf(as.get("a")));

		final AlgorithmSetting<Double> asd = new AlgorithmSetting<Double>(0.0);
		asd.setValue(Double.valueOf(as.get("a")));
		assertEquals(14.05, asd.getValue());
	}
}
