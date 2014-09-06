package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class CostWeightedProductFunctionTest extends TestCase {
	public void testCostWeightedProductFunction() {
		final Statistics statistics = TestHelper.getStatistics();

		CostWeightedProductFunction function = new CostWeightedProductFunction();
		function.addParameter("getKelly", 0.8);
		final Double result = function.calculate(statistics);
		assertEquals(0.277441, result, Settings.doubleEpsilon);
	}
}
