package stsc.algorithms;

import stsc.common.algorithms.AlgorithmSetting;
import junit.framework.TestCase;

public final class AlgorithmSettingTest extends TestCase {
	public void testAlgorithmSettingWithDouble() {
		AlgorithmSettingImpl<Double> asd = new AlgorithmSettingImpl<Double>(new Double(0.0));
		assertEquals(0.0, asd.getValue());
		asd.setValue(new Double(5.3));
		assertEquals(5.3, asd.getValue());
		asd.setValue(7.6);
		assertEquals(7.6, asd.getValue());
	}

	public void testAlgorithmSettingWithInteger() {
		AlgorithmSettingImpl<Integer> asi = new AlgorithmSettingImpl<Integer>(Integer.valueOf(45));
		assertEquals(45, asi.getValue().intValue());
		asi.setValue(67);
		assertEquals(67, asi.getValue().intValue());
	}

	public void testAlgorithmSettingWithString() {
		AlgorithmSetting<String> ass = new AlgorithmSettingImpl<String>("str");
		assertEquals("str", ass.getValue());
		AlgorithmSettingImpl<String> setable = new AlgorithmSettingImpl<String>("");
		setable.setValue("strvalue");
		assertEquals("strvalue", setable.getValue());
	}
}
