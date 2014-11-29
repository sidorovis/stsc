package stsc.general.statistic.cost.comparator;

import java.text.ParseException;
import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsCompareSelector;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestStatisticsHelper;

public class CostMaximumLikelihoodComparatorTest {

	@Test
	public void testCostMaximumLikelihoodComparatorOnSeveral() {
		final CostMaximumLikelihoodComparator comparator = new CostMaximumLikelihoodComparator();
		comparator.addParameter("getKelly", 0.8);
		comparator.addParameter("getWinProb", 0.4);
		comparator.addParameter("getMaxWin", 0.9);
		for (int i = 1; i < 6; ++i) {
			final Statistics leftStat = TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, i));
			for (int u = i + 20; u < 25; ++u) {
				if (i != u) {
					final Statistics rightStat = TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, u));
					final int r = comparator.compare(leftStat, rightStat) * comparator.compare(rightStat, leftStat);
					if (r != 0)
						Assert.assertEquals(-1, r);
				}
			}
		}
	}

	@Test
	public void testCostStatisticsCompareSelectorWithLikelihood() throws ParseException {
		final CostMaximumLikelihoodComparator c = new CostMaximumLikelihoodComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, Day.createDate("08-05-2013"))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, Day.createDate("04-05-2013"))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, Day.createDate("16-05-2013"))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, Day.createDate("12-05-2013"))));

		Assert.assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		
		Assert.assertEquals(0.358820, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(-0.201986, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(-1.288391, si.next().getAvGain(), Settings.doubleEpsilon);
	}

	@Test
	public void testCostStatisticsCompareSelectorWithLikelihoodWithKelly() {
		final CostMaximumLikelihoodComparator c = new CostMaximumLikelihoodComparator();
		c.addParameter("getMaxLoss", 100.0);
		c.addParameter("getAvGain", -50.0);
		c.addParameter("getFreq", 15.0);
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16))));
		sel.addStrategy(TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12))));

		Assert.assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		Assert.assertEquals(-0.201986, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.358820, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(-1.288391, si.next().getAvGain(), Settings.doubleEpsilon);
	}
}
