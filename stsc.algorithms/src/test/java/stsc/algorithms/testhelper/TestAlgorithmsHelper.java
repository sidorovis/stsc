package stsc.algorithms.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;

public class TestAlgorithmsHelper {

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(getPeriod());
	}

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return null;
	}
}
