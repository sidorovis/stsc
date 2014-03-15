package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.common.Stock;
import stsc.signals.StockSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import junit.framework.TestCase;

public class StockAlgorithmTest extends TestCase {
	private static class StockAlgorithmHelper extends StockAlgorithm {

		public StockAlgorithmHelper(String stockName, String executionName, SignalsStorage signalsStorage,
				AlgorithmSettings algorithmSettings) {
			super(stockName, executionName, signalsStorage, algorithmSettings);
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
		SignalsStorage ss = new SignalsStorage();
		AlgorithmSettings as = new AlgorithmSettings();
		StockAlgorithmHelper sah = new StockAlgorithmHelper("a", "s", ss, as);
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(StockSignal.class, ss.getStockSignal("a", "s", 0).getValue().getClass());
	}
}
