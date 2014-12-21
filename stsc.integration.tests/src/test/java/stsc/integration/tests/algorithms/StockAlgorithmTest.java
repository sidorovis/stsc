package stsc.integration.tests.algorithms;

import java.text.ParseException;
import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.series.LimitSignalsSerie;
import junit.framework.TestCase;

public final class StockAlgorithmTest extends TestCase {
	private static class StockAlgorithmHelper extends StockAlgorithm {

		public StockAlgorithmHelper(final StockAlgorithmInit init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<SerieSignal> registerSignalsClass(final StockAlgorithmInit init) throws BadAlgorithmException {
			return new LimitSignalsSerie<>(SerieSignal.class);
		}

		@Override
		public void process(Day day) throws BadSignalException {
			addSignal(new Date(), new SerieSignal());
		}
	}

	public void testStockAlgorithm() throws BadSignalException, BadAlgorithmException, ParseException {
		StockAlgoInitHelper init = new StockAlgoInitHelper("s", "a");
		StockAlgorithmHelper sah = new StockAlgorithmHelper(init.getInit());
		final Date d = new Date();
		sah.process(new Day(d));
		assertEquals(SerieSignal.class, init.getStorage().getStockSignal("a", "s", 0).getValue().getClass());
	}
}
