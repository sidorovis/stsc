package stsc.algorithms;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.common.algorithms.AlgorithmSetting;

public final class AlgorithmSettingTest {

	@Test
	public void testAlgorithmSettingWithDouble() {
		AlgorithmSettingImpl<Double> asd = new AlgorithmSettingImpl<Double>(new Double(0.0));
		Assert.assertEquals(0.0, asd.getValue(), Settings.doubleEpsilon);
		asd.setValue(new Double(5.3));
		Assert.assertEquals(5.3, asd.getValue(), Settings.doubleEpsilon);
		asd.setValue(7.6);
		Assert.assertEquals(7.6, asd.getValue(), Settings.doubleEpsilon);
	}

	@Test
	public void testAlgorithmSettingWithInteger() {
		AlgorithmSettingImpl<Integer> asi = new AlgorithmSettingImpl<Integer>(Integer.valueOf(45));
		Assert.assertEquals(45, asi.getValue().intValue());
		asi.setValue(67);
		Assert.assertEquals(67, asi.getValue().intValue());
	}

	@Test
	public void testAlgorithmSettingWithString() {
		AlgorithmSetting<String> ass = new AlgorithmSettingImpl<String>("str");
		Assert.assertEquals("str", ass.getValue());
		AlgorithmSettingImpl<String> setable = new AlgorithmSettingImpl<String>("");
		setable.setValue("strvalue");
		Assert.assertEquals("strvalue", setable.getValue());
	}
}
