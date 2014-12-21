package stsc.integration.tests.algorithms;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.signals.series.LimitSignalsSerie;

public final class EodAlgorithmTest extends TestCase {

	private static class EodAlgorithmHelper extends EodAlgorithm {

		protected EodAlgorithmHelper(EodAlgorithmInit init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<StockSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
			return new LimitSignalsSerie<>(StockSignal.class);
		}

		@Override
		public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
			addSignal(date, new StockSignal());
		}

	}

	public void testEodAlgorithm() throws BadSignalException, BadAlgorithmException {
		EodAlgoInitHelper init = new EodAlgoInitHelper("a");
		EodAlgorithmHelper eah = new EodAlgorithmHelper(init.getInit());
		final Date theDate = new Date();
		eah.process(new Date(), new HashMap<String, Day>());
		assertEquals(StockSignal.class, init.getStorage().getEodSignal("a", theDate).getSignal(StockSignal.class).getClass());
	}
}
