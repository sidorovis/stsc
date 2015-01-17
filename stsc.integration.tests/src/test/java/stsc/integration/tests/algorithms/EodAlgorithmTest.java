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
import stsc.common.signals.SerieSignal;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.signals.series.LimitSignalsSerie;

public final class EodAlgorithmTest extends TestCase {

	private static class EodAlgorithmHelper extends EodAlgorithm {

		protected EodAlgorithmHelper(EodAlgorithmInit init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<SerieSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
			return new LimitSignalsSerie<>(SerieSignal.class);
		}

		@Override
		public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
			addSignal(date, new SerieSignal());
		}

	}

	public void testEodAlgorithm() throws BadSignalException, BadAlgorithmException {
		EodAlgoInitHelper init = new EodAlgoInitHelper("a");
		EodAlgorithmHelper eah = new EodAlgorithmHelper(init.getInit());
		final Date theDate = new Date();
		eah.process(new Date(), new HashMap<String, Day>());
		assertEquals(SerieSignal.class, init.getStorage().getEodSignal("a", theDate).getContent(SerieSignal.class).getClass());
	}
}
