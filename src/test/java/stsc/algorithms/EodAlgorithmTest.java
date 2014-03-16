package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class EodAlgorithmTest extends TestCase {

	private static class EodAlgorithmHelper extends EodAlgorithm {

		protected EodAlgorithmHelper(String executionName, Broker broker, SignalsStorage signalsStorage,
				AlgorithmSettings algorithmSettings) {
			super(executionName, broker, signalsStorage, algorithmSettings);
		}

		@Override
		public Class<? extends EodSignal> registerSignalsClass() {
			return EodSignal.class;
		}

		@Override
		public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
			addSignal(date, new EodSignal());
		}

	}

	public void testEodAlgorithm() throws BadSignalException {
		final SignalsStorage ss = new SignalsStorage();
		final Broker b = new Broker(new ThreadSafeStockStorage());
		final AlgorithmSettings settings = new AlgorithmSettings();
		EodAlgorithmHelper eah = new EodAlgorithmHelper("a", b, ss, settings);
		final Date theDate = new Date();
		eah.process(new Date(), new HashMap<String, Day>());
		assertEquals(EodSignal.class, ss.getEodSignal("a", theDate).getSignal(EodSignal.class).getClass());
	}
}
