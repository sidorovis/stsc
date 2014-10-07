package stsc.general.statistic.cost.function;

import java.text.ParseException;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class CostWeightedSumFunctionTest extends TestCase {
	public void testCostWeightedSumFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostWeightedSumFunction function = new CostWeightedSumFunction();
		final Double result = function.calculate(statistics);
		assertEquals(-0.242883, result, Settings.doubleEpsilon);

		function.addParameter("getPeriod", 0.5);
		final Double result2 = function.calculate(statistics);
		assertEquals(0.757116, result2, Settings.doubleEpsilon);

		function.addParameter("getKelly", 0.3);
		final Double result3 = function.calculate(statistics);
		assertEquals(0.757116, result3, Settings.doubleEpsilon);

		function.addParameter("getMaxLoss", 0.7);
		final Double result4 = function.calculate(statistics);
		assertEquals(127.457116, result4, Settings.doubleEpsilon);
	}
}
