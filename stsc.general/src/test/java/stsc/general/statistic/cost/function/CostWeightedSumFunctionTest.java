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
		final Double expectedResult = 1.0 * statistics.getAvGain();
		final Double result = function.calculate(statistics);
		assertEquals(expectedResult, result, Settings.doubleEpsilon);

		function.addParameter("getPeriod", 0.5);
		final Double expectedResult2 = expectedResult + statistics.getPeriod() * 0.5;
		final Double result2 = function.calculate(statistics);
		assertEquals(expectedResult2, result2, Settings.doubleEpsilon);

		function.addParameter("getKelly", 0.3);
		final Double expectedResult3 = expectedResult2 + statistics.getKelly() * 0.3;
		final Double result3 = function.calculate(statistics);
		assertEquals(expectedResult3, result3, Settings.doubleEpsilon);

		function.addParameter("getMaxLoss", 0.7);
		final Double expectedResult4 = expectedResult3 + statistics.getMaxLoss() * 0.7;
		final Double result4 = function.calculate(statistics);
		assertEquals(expectedResult4, result4, Settings.doubleEpsilon);
	}
}
