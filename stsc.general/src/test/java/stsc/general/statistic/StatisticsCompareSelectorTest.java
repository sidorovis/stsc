package stsc.general.statistic;

import java.util.Iterator;

import org.joda.time.LocalDate;

import stsc.common.Settings;
import stsc.general.statistic.cost.comparator.CostStatisticsComparator;
import stsc.general.statistic.cost.comparator.CostWeightedSumComparator;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsCompareSelectorTest extends TestCase {
	public void testStatisticsCompareSelector() {
		final CostStatisticsComparator c = new CostWeightedSumComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		sel.addStrategy(TradingStrategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8))));
		sel.addStrategy(TradingStrategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4))));
		sel.addStrategy(TradingStrategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16))));
		sel.addStrategy(TradingStrategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12))));

		assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		assertEquals(2.900946, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.195823, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-0.929453, si.next().getAvGain(), Settings.doubleEpsilon);
	}

}
