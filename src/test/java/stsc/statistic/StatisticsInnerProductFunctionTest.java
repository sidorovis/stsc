package stsc.statistic;

import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsInnerProductFunctionTest extends TestCase {
	public void testStatisticsInnerProductFunction() {
		final Statistics statistics = TestHelper.getStatistics();

		final StatisticsInnerProductFunction function = new StatisticsInnerProductFunction();
		final Double result = function.calculate(statistics);
		assertEquals(0.246987, result, 0.000001);

		function.addParameter("getPeriod", 0.5);
		final Double result2 = function.calculate(statistics);
		assertEquals(1.246987, result2, 0.000001);

		function.addParameter("getKelly", 0.3);
		final Double result3 = function.calculate(statistics);
		assertEquals(1.240465, result3, 0.000001);

		function.addParameter("getMaxWin", 0.7);
		final Double result4 = function.calculate(statistics);
		assertEquals(49.540465, result4, 0.000001);
	}
}
