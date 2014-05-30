package stsc.general.storage;

import java.text.ParseException;

import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.general.storage.ExecutionStarter;
import stsc.general.storage.ExecutionsStorage;
import stsc.general.testhelper.TestAlgorithmsHelper;
import stsc.general.testhelper.TestStockStorageHelper;
import stsc.general.trading.BrokerImpl;
import junit.framework.TestCase;

public class ExecutionsStorageTest extends TestCase {

	public void testExecutionsStorage() throws BadAlgorithmException {
		final AlgorithmSettings smaSettings = TestAlgorithmsHelper.getSettings().addSubExecutionName("asd");

		final ExecutionsStorage eStorage = new ExecutionsStorage();

		eStorage.addStockExecution(new StockExecution("t2", Sma.class, smaSettings));
		eStorage.addEodExecution(new EodExecution("t1", TestingEodAlgorithm.class, TestAlgorithmsHelper.getSettings()));
		ExecutionStarter es = eStorage.initialize(new BrokerImpl(new TestStockStorageHelper()));

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
			es.initialize(new BrokerImpl(new TestStockStorageHelper()));
		} catch (BadAlgorithmException e) {
			throwed = true;
		}
		assertEquals(true, throwed);
	}
}
