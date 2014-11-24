package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;

public class CostWeightedProductFunctionTest {

	@Test
	public void testCostWeightedProductFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostWeightedProductFunction function = new CostWeightedProductFunction();
		function.addParameter("getKelly", 0.8);
		final Double expectedResult = Math.signum(statistics.getAvGain()) * Math.pow(Math.abs(statistics.getAvGain()), 1.0 / 1.8)
				+ Math.signum(statistics.getKelly()) * Math.pow(Math.abs(statistics.getKelly()), 0.8 / 1.8);
		final Double result = function.calculate(statistics);
		Assert.assertEquals(expectedResult, result, Settings.doubleEpsilon);
	}
}
