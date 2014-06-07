package stsc.general.statistic.cost.function;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class BayesianProbabilityCostFunctionTest extends TestCase {
	public void testBayesianProbabilityCostFunction() {
		final Statistics statistics = TestHelper.getStatistics();

		final BayesianProbabilityCostFunction bayesian = new BayesianProbabilityCostFunction();
		bayesian.addLayer().put("getPeriod", 10.0);
		bayesian.addLayer().put("getAvGain", 12.0);
		final Double bayesianResult = bayesian.calculate(statistics);
		assertEquals(2.963845, bayesianResult, Settings.doubleEpsilon);
	}
}
