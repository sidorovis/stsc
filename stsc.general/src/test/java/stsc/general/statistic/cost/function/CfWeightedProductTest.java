package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class CfWeightedProductTest extends TestCase {
	public void testCfWeightedProduct() {
		final Statistics statistics = TestHelper.getStatistics();

		CfWeightedProduct function = new CfWeightedProduct();
		function.addParameter("getKelly", 0.8);
		final Double result = function.calculate(statistics);
		assertEquals(0.277441, result, Settings.doubleEpsilon);
	}
}
