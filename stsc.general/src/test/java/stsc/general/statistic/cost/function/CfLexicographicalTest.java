package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class CfLexicographicalTest extends TestCase {
	public void testCfLexicographical() {
		final Statistics statistics = TestHelper.getStatistics();

		final LexicographicalCostFunction c10 = new LexicographicalCostFunction();
		c10.addNextValue("getPeriod");
		c10.addNextValue("getAvGain");
		final Double c10result = c10.calculate(statistics);
		assertEquals(20.246987, c10result, Settings.doubleEpsilon);
	}
	public void testCfLexicographical100() {
		final Statistics statistics = TestHelper.getStatistics();

		final LexicographicalCostFunction c100 = new LexicographicalCostFunction(100);
		c100.addNextValue("getPeriod");
		c100.addNextValue("getAvGain");
		c100.addNextValue("getAvGain");
		final Double c100result = c100.calculate(statistics);
		assertEquals(20024.9456978, c100result, Settings.doubleEpsilon);
	}

	public void testCfLexicographicalAnotherOrder() {
		final Statistics statistics = TestHelper.getStatistics();

		final LexicographicalCostFunction c10 = new LexicographicalCostFunction();
		c10.addNextValue("getAvGain");
		c10.addNextValue("getPeriod");
		final Double c10result = c10.calculate(statistics);
		assertEquals(4.469871, c10result, Settings.doubleEpsilon);
	}
}
