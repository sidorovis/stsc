package stsc.storage;

import java.util.Arrays;
import java.util.List;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.StockExecution;
import stsc.algorithms.factors.primitive.Sma;
import stsc.algorithms.primitive.TestingEodAlgorithm;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class ExecutionsStorageTest extends TestCase {
	public void testExecutionsStorage() throws BadAlgorithmException {
		final List<String> stocks = Arrays.asList(new String[] { "aapl", "goog", "epl" });

		final SignalsStorage signalsStorage = new SignalsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());

		AlgorithmSettings smaSettings = new AlgorithmSettings();
		smaSettings.addSubExecutionName("asd");

		final ExecutionsStorage es = new ExecutionsStorage(stocks);
		es.addStockExecution(new StockExecution("t2", Sma.class, smaSettings));
		es.addEodExecution(new EodExecution("t1", TestingEodAlgorithm.class, new AlgorithmSettings()));

		es.initializeExecutions(signalsStorage, broker);

		assertEquals(1, es.getEodAlgorithmsSize());

		assertNotNull(es.getEodAlgorithm("t1"));
		assertNull(es.getEodAlgorithm("t2"));

		assertNotNull(es.getStockAlgorithm("t2", "aapl"));
		assertNotNull(es.getStockAlgorithm("t2", "goog"));
		assertNotNull(es.getStockAlgorithm("t2", "epl"));

		assertNull(es.getStockAlgorithm("t1", "aapl"));
		assertNull(es.getStockAlgorithm("t1", "goog"));
		assertNull(es.getStockAlgorithm("t1", "epl"));

		assertNull(es.getStockAlgorithm("t2", "epl2"));
	}

	public void testExceptionOnInit() throws BadAlgorithmException {
		final List<String> stocks = Arrays.asList(new String[] { "aapl", "goog", "epl" });
		final ExecutionsStorage es = new ExecutionsStorage(stocks);
		es.addStockExecution(new StockExecution("t2", Sma.class, new AlgorithmSettings()));
		final SignalsStorage signalsStorage = new SignalsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());

		boolean throwed = false;
		try {
			es.initializeExecutions(signalsStorage, broker);
		} catch (BadAlgorithmException e) {
			throwed = true;
		}
		assertEquals(true, throwed);
	}
}
