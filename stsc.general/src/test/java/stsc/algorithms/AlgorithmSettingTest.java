package stsc.algorithms;

import junit.framework.TestCase;

public class AlgorithmSettingTest extends TestCase {
	public void testAlgorithmSettingWithDouble() {
		AlgorithmSetting<Double> asd = new AlgorithmSetting<Double>(new Double(0.0));
		assertEquals(0.0, asd.getValue());
		asd.setValue(new Double(5.3));
		assertEquals(5.3, asd.getValue());
		asd.setValue(7.6);
		assertEquals(7.6, asd.getValue());
	}

	public void testAlgorithmSettingWithInteger() {
		AlgorithmSetting<Integer> asi = new AlgorithmSetting<Integer>(Integer.valueOf(45));
		assertEquals(45, asi.getValue().intValue());
		asi.setValue(67);
		assertEquals(67, asi.getValue().intValue());
	}

	public void testAlgorithmSettingWithString() {
		AlgorithmSetting<String> ass = new AlgorithmSetting<String>("str");
		assertEquals("str", ass.getValue());
		AlgorithmSetting<String> setable = new AlgorithmSetting<String>("");
		setable.setValue("strvalue");
		assertEquals("strvalue", setable.getValue());
	}
}
