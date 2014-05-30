package stsc.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.series.LimitSignalsSerie;
import stsc.testhelper.StockAlgoInitHelper;
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
		StockAlgoInitHelper init = new StockAlgoInitHelper("s", "a");
		StockAlgorithmHelper sah = new StockAlgorithmHelper(init.getInit());
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(StockSignal.class, init.getStorage().getStockSignal("a", "s", 0).getValue().getClass());
	}
}
