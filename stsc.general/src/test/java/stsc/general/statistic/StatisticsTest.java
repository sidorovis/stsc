package stsc.general.statistic;

import java.util.Set;

import stsc.general.statistic.Statistics;
import junit.framework.TestCase;

public class StatisticsTest extends TestCase {
	public void testStatistics() {
		final Set<String> names = Statistics.getStatisticsMethods();
		assertEquals(23, names.size());
		assertTrue(names.contains("getFreq"));
		assertTrue(names.contains("getMaxWin"));
		assertTrue(names.contains("getDdValueAvGain"));
		assertTrue(names.contains("getAvGain"));
	}
}
