package stsc.algorithms.eod.privitive;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.eod.primitive.PositionNDayMStocks;
import stsc.simulator.Simulator;
import stsc.statistic.Statistics;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class PositionNDayMStocksTest extends TestCase {
	public void testPositionNDayMStocksException() {
		try {
			final EodAlgorithm.Init init = TestHelper.getEodAlgorithmInit();
			new PositionNDayMStocks(init);
			fail("PositionNDayMStocks algo ");
		} catch (Exception e) {
			assertTrue(e instanceof BadAlgorithmException);
		}
	}

	public void testPositionNDayMStocks() throws Exception {
		Statistics s = Simulator.fromFile("./test_data/simulator_tests/ndays.ini").getStatistics();
		assertNotNull(s);
		assertEquals(550, s.getPeriod());
		assertEquals(69.255712, s.getAvGain(), 0.000001);
	}
}
