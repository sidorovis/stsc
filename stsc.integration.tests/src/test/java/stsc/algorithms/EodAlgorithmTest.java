package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.EodSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.series.LimitSignalsSerie;
import stsc.testhelper.EodAlgoInitHelper;
import junit.framework.TestCase;

public final class EodAlgorithmTest extends TestCase {

	private static class EodAlgorithmHelper extends EodAlgorithm {

		protected EodAlgorithmHelper(EodAlgorithmInit init) throws BadAlgorithmException {
			super(init);
		}

		@Override
		public SignalsSerie<EodSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
			return new LimitSignalsSerie<>(EodSignal.class);
		}

		@Override
		public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
			addSignal(date, new EodSignal());
		}

	}

	public void testEodAlgorithm() throws BadSignalException, BadAlgorithmException {
		EodAlgoInitHelper init = new EodAlgoInitHelper("a");
		EodAlgorithmHelper eah = new EodAlgorithmHelper(init.getInit());
		final Date theDate = new Date();
		eah.process(new Date(), new HashMap<String, Day>());
		assertEquals(EodSignal.class, init.getStorage().getEodSignal("a", theDate).getSignal(EodSignal.class).getClass());
	}
}
