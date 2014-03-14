package stsc.algorithms;

import junit.framework.TestCase;

public class AlgorithmSettingsTest extends TestCase {
	public void testAlgorithmsSettings() {
		final AlgorithmSettings as = new AlgorithmSettings();
		final String d = as.get("a");
		assertNull(d);
		assertNotNull(as.set("a", new Double(14.05)));
		assertNotNull(as.set("b", 14.05));
		Double recD = new Double(0.0);
		as.get("b", recD);
		double recV = 0.0;
		as.get("a", recV);
		assertEquals(recD.doubleValue(), recV);

		as.set("r", "14.56");
		assertEquals(14.56, Double.valueOf(as.get("r")));

	}
}
