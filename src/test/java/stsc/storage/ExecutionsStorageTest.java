package stsc.storage;

import java.util.Arrays;
import java.util.List;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.algorithms.factors.primitive.Sma;
import stsc.algorithms.primitive.TestingEodAlgorithm;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class ExecutionsStorageTest extends TestCase {
	public void testExecutionsStorage() throws BadAlgorithmException {
		final List<EodAlgorithmExecution> eae = Arrays.asList(new EodAlgorithmExecution[] { new EodAlgorithmExecution(
				"t1", TestingEodAlgorithm.class) });
		final List<StockAlgorithmExecution> sae = Arrays
				.asList(new StockAlgorithmExecution[] { new StockAlgorithmExecution("t2", Sma.class) });
		final List<String> stocks = Arrays.asList(new String[] { "aapl", "goog", "epl" });

		StockStorage stockStorage = new ThreadSafeStockStorage();
		SignalsStorage signals = new SignalsStorage();
		Broker broker = new Broker(stockStorage);
		ExecutionsStorage es = new ExecutionsStorage(sae, eae, stocks, broker, signals);

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
}
