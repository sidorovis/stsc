package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.StockSignal;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class StockAlgorithmTest extends TestCase {
	private static class StockAlgorithmHelper extends StockAlgorithm {

		public StockAlgorithmHelper(final StockAlgorithm.Init init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<StockSignal> registerSignalsClass() {
			return new LimitSignalsSerie<>(StockSignal.class);
		}

		@Override
		public void process(Day day) throws BadSignalException {
			addSignal(new Date(), new StockSignal());
		}
	}

	public void testStockAlgorithm() throws BadSignalException, BadAlgorithmException {
		StockAlgorithm.Init init = TestAlgorithmsHelper.getStockAlgorithmInit();
		init.executionName = "s";
		init.stockName = "a";
		StockAlgorithmHelper sah = new StockAlgorithmHelper(init);
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(StockSignal.class, init.signalsStorage.getStockSignal("a", "s", 0).getValue().getClass());
	}
}
