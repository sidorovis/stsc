package stsc.algorithms.eod.privitive;

import stsc.algorithms.eod.primitive.PositionNDayMStocks;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.simulator.Simulator;
import stsc.statistic.Statistics;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class PositionNDayMStocksTest extends TestCase {
	public void testPositionNDayMStocksException() {
		try {
			final EodAlgorithmInit init = TestAlgorithmsHelper.getEodAlgorithmInit();
			new PositionNDayMStocks(init);
			fail("PositionNDayMStocks algo ");
		} catch (Exception e) {
			assertTrue(e instanceof BadAlgorithmException);
		}
	}

	public void testPositionNDayMStocks() throws Exception {
		Statistics s = Simulator.fromFile("./test_data/simulator_tests/ndays.ini").getStatistics();
		assertNotNull(s);
		assertEquals(550.0, s.getPeriod());
		assertEquals(69.255712, s.getAvGain(), Settings.doubleEpsilon);
	}
}
