package stsc.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.SignalsSerie;
import stsc.common.StockSignal;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public final class StockAlgorithmTest extends TestCase {
	private static class StockAlgorithmHelper extends StockAlgorithm {

		public StockAlgorithmHelper(final StockAlgorithmInit init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<StockSignal> registerSignalsClass(final StockAlgorithmInit init) throws BadAlgorithmException {
			return new LimitSignalsSerie<>(StockSignal.class);
		}

		@Override
		public void process(Day day) throws BadSignalException {
			addSignal(new Date(), new StockSignal());
		}
	}

	public void testStockAlgorithm() throws BadSignalException, BadAlgorithmException {
		StockAlgorithmInit init = TestAlgorithmsHelper.getStockAlgorithmInit();
		init.executionName = "s";
		init.stockName = "a";
		StockAlgorithmHelper sah = new StockAlgorithmHelper(init);
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(StockSignal.class, init.signalsStorage.getStockSignal("a", "s", 0).getValue().getClass());
	}
}
