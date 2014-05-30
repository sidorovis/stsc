package stsc.testhelper;

import stsc.common.FromToPeriod;

public class TestHelper {

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return null;
	}

}
