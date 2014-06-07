package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class WeightedProductCostFunctionTest extends TestCase {
	public void testWeightedProductCostFunction() {
		final Statistics statistics = TestHelper.getStatistics();

		WeightedProductCostFunction function = new WeightedProductCostFunction();
		function.addParameter("getKelly", 0.8);
		final Double result = function.calculate(statistics);
		assertEquals(0.277441, result, Settings.doubleEpsilon);
	}
}
