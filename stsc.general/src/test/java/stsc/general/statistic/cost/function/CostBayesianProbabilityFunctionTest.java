package stsc.general.statistic.cost.function;

import java.text.ParseException;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class CostBayesianProbabilityFunctionTest extends TestCase {

	public void testBayesianProbabilityCostFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostBayesianProbabilityFunction bayesian = new CostBayesianProbabilityFunction();
		bayesian.addLayer().put("getPeriod", 10.0);
		bayesian.addLayer().put("getAvGain", 12.0);
		final Double bayesianResult = bayesian.calculate(statistics);
		assertEquals(2.963845, bayesianResult, Settings.doubleEpsilon);
	}
}
