package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;

public class CostLexicographicalFunctionTest {

	@Test
	public void testLexicographicalCostFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getPeriod");
		c10.addNextValue("getAvGain");
		final Double expectedResult = statistics.getPeriod() * 10 + statistics.getAvGain();
		final Double c10result = c10.calculate(statistics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunction100() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c100 = new CostLexicographicalFunction(100);
		c100.addNextValue("getPeriod");
		c100.addNextValue("getAvGain");
		c100.addNextValue("getAvGain");
		final Double expectedResult = (statistics.getPeriod() * 100 + statistics.getAvGain()) * 100 + statistics.getAvGain();
		final Double c100result = c100.calculate(statistics);
		Assert.assertEquals(expectedResult, c100result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunctionAnotherOrder() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getAvGain");
		c10.addNextValue("getPeriod");
		final Double expectedResult = statistics.getAvGain() * 10 + statistics.getPeriod();
		final Double c10result = c10.calculate(statistics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}
}
