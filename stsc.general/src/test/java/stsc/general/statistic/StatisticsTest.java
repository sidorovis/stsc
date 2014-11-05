package stsc.general.statistic;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.Statistics;

public class StatisticsTest {

	@Test
	public void testStatistics() {
		final Set<String> names = Statistics.getStatisticsMethods();
		Assert.assertEquals(23, names.size());
		Assert.assertTrue(names.contains("getFreq"));
		Assert.assertTrue(names.contains("getMaxWin"));
		Assert.assertTrue(names.contains("getDdValueAvGain"));
		Assert.assertTrue(names.contains("getAvGain"));
	}
}
