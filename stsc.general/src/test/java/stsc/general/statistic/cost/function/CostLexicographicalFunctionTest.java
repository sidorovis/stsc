package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class CostLexicographicalFunctionTest extends TestCase {
	public void testLexicographicalCostFunction() {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getPeriod");
		c10.addNextValue("getAvGain");
		final Double c10result = c10.calculate(statistics);
		assertEquals(20.246987, c10result, Settings.doubleEpsilon);
	}
	public void testLexicographicalCostFunction100() {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c100 = new CostLexicographicalFunction(100);
		c100.addNextValue("getPeriod");
		c100.addNextValue("getAvGain");
		c100.addNextValue("getAvGain");
		final Double c100result = c100.calculate(statistics);
		assertEquals(20024.9456978, c100result, Settings.doubleEpsilon);
	}

	public void testLexicographicalCostFunctionAnotherOrder() {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getAvGain");
		c10.addNextValue("getPeriod");
		final Double c10result = c10.calculate(statistics);
		assertEquals(4.469871, c10result, Settings.doubleEpsilon);
	}
}
