package stsc.general.statistic;

import java.util.Iterator;

import org.joda.time.LocalDate;

import stsc.general.statistic.cost.comparator.StatisticsComparator;
import stsc.general.statistic.cost.comparator.WeightedSumComparator;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsCompareSelectorTest extends TestCase {
	public void testStatisticsCompareSelector() {
		final StatisticsComparator c = new WeightedSumComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12)));

		assertEquals(3, sel.getSortedStatistics().size());
		final Iterator<Statistics> si = sel.getSortedStatistics().iterator();
		assertEquals(2.900946, si.next().getAvGain(), 0.000001);
		assertEquals(0.195823, si.next().getAvGain(), 0.000001);
		assertEquals(-0.929453, si.next().getAvGain(), 0.000001);
	}
}
