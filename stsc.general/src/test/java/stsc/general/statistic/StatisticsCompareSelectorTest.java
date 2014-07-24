package stsc.general.statistic;

import java.util.Iterator;

import org.joda.time.LocalDate;

import stsc.common.Settings;
import stsc.general.statistic.cost.comparator.StatisticsComparator;
import stsc.general.statistic.cost.comparator.WeightedSumComparator;
import stsc.general.strategy.Strategy;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsCompareSelectorTest extends TestCase {
	public void testStatisticsCompareSelector() {
		final StatisticsComparator c = new WeightedSumComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		sel.addStrategy(Strategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8))));
		sel.addStrategy(Strategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4))));
		sel.addStrategy(Strategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16))));
		sel.addStrategy(Strategy.createTest(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12))));

		assertEquals(3, sel.getStrategies().size());
		final Iterator<Strategy> si = sel.getStrategies().iterator();
		assertEquals(2.900946, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.195823, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-0.929453, si.next().getAvGain(), Settings.doubleEpsilon);
	}

}
