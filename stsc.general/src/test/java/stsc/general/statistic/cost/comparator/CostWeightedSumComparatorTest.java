package stsc.general.statistic.cost.comparator;

import java.text.ParseException;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;

public class CostWeightedSumComparatorTest {

	@Test
	public void testCostWeightedSumComparator() throws ParseException {
		final Statistics stat = TestStatisticsHelper.getStatistics();

		final CostWeightedSumComparator comparator = new CostWeightedSumComparator();
		comparator.addParameter("getKelly", 0.8);

		Assert.assertEquals(0, comparator.compare(stat, stat));

		final Statistics newStat = TestStatisticsHelper.getStatistics(50, 150, new LocalDate(2013, 5, 1));
		Assert.assertEquals(0, comparator.compare(newStat, newStat));

		Assert.assertEquals(-1, comparator.compare(stat, newStat));
		Assert.assertEquals(1, comparator.compare(newStat, stat));
	}

	@Test
	public void testCostWeightedSumComparatorOnSeveralStatistics() {
		final CostWeightedSumComparator comparator = new CostWeightedSumComparator();
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
}
