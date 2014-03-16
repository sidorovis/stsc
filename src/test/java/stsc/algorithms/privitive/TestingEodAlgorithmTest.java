package stsc.algorithms.privitive;

import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.primitive.TestingEodAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class TestingEodAlgorithmTest extends TestCase {
	public void testTestingEodAlgorithm() throws BadSignalException {
		TestingEodAlgorithm tea = new TestingEodAlgorithm("a", new Broker(new ThreadSafeStockStorage()),
				new SignalsStorage(), new AlgorithmSettings());
		tea.process(new Date(), new HashMap<String, Day>());
	}
}
