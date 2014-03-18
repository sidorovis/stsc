package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.StockSignal;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StockAlgorithmTest extends TestCase {
	private static class StockAlgorithmHelper extends StockAlgorithm {

		public StockAlgorithmHelper(final StockAlgorithm.Init init) {
			super(init);
		}

		@Override
		public Class<? extends StockSignal> registerSignalsClass() {
			return StockSignal.class;
		}

		@Override
		public void process(Day day) throws BadSignalException {
			addSignal(new Date(), new StockSignal());
		}
	}

	public void testStockAlgorithm() throws BadSignalException {
		StockAlgorithm.Init init = TestHelper.getStockAlgorithmInit();
		init.executionName = "s";
		init.stockName = "a";
		StockAlgorithmHelper sah = new StockAlgorithmHelper(init);
		sah.registerAlgorithmClass();
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(StockSignal.class, init.signalsStorage.getStockSignal("a", "s", 0).getValue().getClass());
	}
}
