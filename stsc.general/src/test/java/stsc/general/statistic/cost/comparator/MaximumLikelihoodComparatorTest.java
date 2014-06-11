package stsc.general.statistic.cost.comparator;

import java.util.Iterator;

import org.joda.time.LocalDate;

import stsc.common.Settings;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsCompareSelector;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class MaximumLikelihoodComparatorTest extends TestCase {

	public void testMaximumLikelihoodComparatorOnSeveral() {
		final MaximumLikelihoodComparator comparator = new MaximumLikelihoodComparator();
		comparator.addParameter("getKelly", 0.8);
		comparator.addParameter("getWinProb", 0.4);
		comparator.addParameter("getMaxWin", 0.9);
		for (int i = 1; i < 6; ++i) {
			final Statistics leftStat = TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, i));
			for (int u = i + 20; u < 25; ++u) {
				if (i != u) {
					final Statistics rightStat = TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, u));
					final int r = comparator.compare(leftStat, rightStat) * comparator.compare(rightStat, leftStat);
					if (r != 0)
						assertEquals(-1, r);
				}
			}
		}
	}

	public void testStatisticsCompareSelectorWithLikelihood() {
		final MaximumLikelihoodComparator c = new MaximumLikelihoodComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12)));

		assertEquals(3, sel.getStatistics().size());
		final Iterator<Statistics> si = sel.getStatistics().iterator();
		assertEquals(2.900946, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.195823, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-0.929453, si.next().getAvGain(), Settings.doubleEpsilon);
	}

	public void testStatisticsCompareSelectorWithLikelihoodWithKelly() {
		final MaximumLikelihoodComparator c = new MaximumLikelihoodComparator();
		c.addParameter("getKelly", -10.0);
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 8)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 4)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 16)));
		sel.addStatistics(TestHelper.getStatistics(50, 150, new LocalDate(2013, 5, 12)));

		assertEquals(3, sel.getStatistics().size());
		final Iterator<Statistics> si = sel.getStatistics().iterator();
		assertEquals(2.900946, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-0.929453, si.next().getAvGain(), Settings.doubleEpsilon);
		assertEquals(-2.522204, si.next().getAvGain(), Settings.doubleEpsilon);
	}
}
