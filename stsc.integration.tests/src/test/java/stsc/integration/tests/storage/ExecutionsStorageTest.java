package stsc.integration.tests.storage;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.general.trading.BrokerImpl;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;

public class ExecutionsStorageTest {

	@Test
	public void testExecutionsStorage() throws BadAlgorithmException {
		final AlgorithmSettings smaSettings = TestAlgorithmsHelper.getSettings().addSubExecutionName("asd");

		final ExecutionsStorage eStorage = new ExecutionsStorage();

		eStorage.addStockExecution(new StockExecution("t2", Sma.class, smaSettings));
		eStorage.addEodExecution(new EodExecution("t1", TestingEodAlgorithm.class, TestAlgorithmsHelper.getSettings()));
		ExecutionStarter es = eStorage.initialize(new BrokerImpl(new StockStorageMock()));

		Assert.assertEquals(1, es.getEodAlgorithmsSize());

		Assert.assertNotNull(es.getEodAlgorithm("t1"));
		Assert.assertNull(es.getEodAlgorithm("t2"));

		Assert.assertNotNull(es.getStockAlgorithm("t2", "aapl"));
		Assert.assertNotNull(es.getStockAlgorithm("t2", "adm"));
		Assert.assertNotNull(es.getStockAlgorithm("t2", "spy"));

		Assert.assertFalse(es.getStockAlgorithm("t1", "aapl").isPresent());
		Assert.assertFalse(es.getStockAlgorithm("t1", "adm").isPresent());
		Assert.assertFalse(es.getStockAlgorithm("t1", "spy").isPresent());

		Assert.assertFalse(es.getStockAlgorithm("t2", "non").isPresent());
	}

	@Test
	public void testExceptionOnInit() throws BadAlgorithmException, ParseException {
		final ExecutionsStorage es = new ExecutionsStorage();
		es.addStockExecution(new StockExecution("t2", Sma.class, TestAlgorithmsHelper.getSettings()));

		boolean throwed = false;
		try {
			es.initialize(new BrokerImpl(new StockStorageMock()));
		} catch (BadAlgorithmException e) {
			throwed = true;
		}
		Assert.assertEquals(true, throwed);
	}

}
