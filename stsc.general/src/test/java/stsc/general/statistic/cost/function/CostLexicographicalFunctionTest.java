package stsc.general.statistic.cost.function;

import java.text.ParseException;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class CostLexicographicalFunctionTest extends TestCase {
	public void testLexicographicalCostFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getPeriod");
		c10.addNextValue("getAvGain");
		final Double c10result = c10.calculate(statistics);
		assertEquals(19.757116, c10result, Settings.doubleEpsilon);
	}

	public void testLexicographicalCostFunction100() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c100 = new CostLexicographicalFunction(100);
		c100.addNextValue("getPeriod");
		c100.addNextValue("getAvGain");
		c100.addNextValue("getAvGain");
		final Double c100result = c100.calculate(statistics);
		assertEquals(19975.468730, c100result, Settings.doubleEpsilon);
	}

	public void testLexicographicalCostFunctionAnotherOrder() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("getAvGain");
		c10.addNextValue("getPeriod");
		final Double c10result = c10.calculate(statistics);
		assertEquals(-0.4288385, c10result, Settings.doubleEpsilon);
	}
}
