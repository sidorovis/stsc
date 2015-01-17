package stsc.integration.tests.helper;

import java.util.Date;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;

public class TestAlgorithmsHelper {

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return new FromToPeriod(new Date(), new Date());
	}

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(getPeriod());
	}

}
