package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;

public class CostBayesianProbabilityFunctionTest {

	private Double calculateTestValue(Statistics statistics) {
		return Math.min(statistics.getPeriod() * 6.0, statistics.getAvGain() * 11.0);
	}

	@Test
	public void testBayesianProbabilityCostFunction() throws ParseException {
		final Statistics statistics = TestStatisticsHelper.getStatistics();

		final CostBayesianProbabilityFunction bayesian = new CostBayesianProbabilityFunction();
		bayesian.addLayer().put("getPeriod", 6.0);
		bayesian.addLayer().put("getAvGain", 11.0);
		final Double bayesianResult = bayesian.calculate(statistics);
		Assert.assertEquals(calculateTestValue(statistics), bayesianResult, Settings.doubleEpsilon);
	}
}
