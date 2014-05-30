package stsc.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;

public class TestAlgorithmsHelper {

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(TestHelper.getPeriod());
	}

}
