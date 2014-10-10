package stsc.general.statistic;

import java.text.ParseException;
import java.util.Iterator;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.cost.comparator.CostWeightedSumComparator;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class StatisticsCompareSelectorTest extends TestCase {
	public void testStatisticsCompareSelector() throws ParseException {
		final CostWeightedSumComparator c = new CostWeightedSumComparator();
		c.addParameter("getWinProb", 5.0);
		c.addParameter("getAvLoss", 14.0);
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		final TradingStrategy ts1 = TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, Day.createDate("08-05-2013")));
		final TradingStrategy ts2 = TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4)));
		final TradingStrategy ts3 = TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16)));
		final TradingStrategy ts4 = TradingStrategy.createTest(TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12)));

		sel.addStrategy(ts1);
		sel.addStrategy(ts2);
		sel.addStrategy(ts3);
		sel.addStrategy(ts4);

		assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		assertEquals(0.590615, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-0.071069, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-1.162257, si.next().getAvGain(), Settings.doubleEpsilon);
	}

}
