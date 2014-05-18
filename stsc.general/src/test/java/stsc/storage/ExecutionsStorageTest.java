package stsc.storage;

import java.text.ParseException;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.StockExecution;
import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.testhelper.TestAlgorithmsHelper;
import stsc.testhelper.TestStockStorageHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class ExecutionsStorageTest extends TestCase {

	public void testExecutionsStorage() throws BadAlgorithmException {
		final AlgorithmSettings smaSettings = TestAlgorithmsHelper.getSettings().addSubExecutionName("asd");

		final ExecutionsStorage es = new ExecutionsStorage();
		es.addStockExecution(new StockExecution("t2", Sma.class, smaSettings));
		es.addEodExecution(new EodExecution("t1", TestingEodAlgorithm.class, TestAlgorithmsHelper.getSettings()));
		es.initialize(new Broker(new TestStockStorageHelper()));

		assertEquals(1, es.getEodAlgorithmsSize());

		assertNotNull(es.getEodAlgorithm("t1"));
		assertNull(es.getEodAlgorithm("t2"));

		assertNotNull(es.getStockAlgorithm("t2", "aapl"));
		assertNotNull(es.getStockAlgorithm("t2", "adm"));
		assertNotNull(es.getStockAlgorithm("t2", "spy"));

		assertNull(es.getStockAlgorithm("t1", "aapl"));
		assertNull(es.getStockAlgorithm("t1", "adm"));
		assertNull(es.getStockAlgorithm("t1", "spy"));

		assertNull(es.getStockAlgorithm("t2", "non"));
	}

	public void testExceptionOnInit() throws BadAlgorithmException, ParseException {
		final ExecutionsStorage es = new ExecutionsStorage();
		es.addStockExecution(new StockExecution("t2", Sma.class, TestAlgorithmsHelper.getSettings()));

		boolean throwed = false;
		try {
			es.initialize(new Broker(new TestStockStorageHelper()));
		} catch (BadAlgorithmException e) {
			throwed = true;
		}
		assertEquals(true, throwed);
	}
}
