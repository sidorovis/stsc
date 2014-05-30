package stsc.general.trading;

import java.io.IOException;
import java.text.ParseException;

import org.joda.time.LocalDate;

import com.google.common.collect.Sets;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.algorithms.eod.primitive.TestingEodAlgorithmSignal;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodExecution;
import stsc.common.signals.EodSignal;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.storage.ExecutionStarter;
import stsc.general.storage.ExecutionsStorage;
import stsc.general.storage.StockStorageFactory;
import stsc.general.storage.ThreadSafeStockStorage;
import stsc.general.trading.TradeProcessor;
import stsc.general.trading.TradeProcessorInit;
import junit.framework.TestCase;

public final class TradeProcessorTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		final String stocksFilePath = "./test_data/trade_processor_tests/";
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, stocksFilePath + stockName + ".csv"));
	}

	public void testTradeProcessor() throws Exception {
		final StockStorage ss = new ThreadSafeStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		final FromToPeriod period = new FromToPeriod("30-10-2013", "06-11-2013");
		final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);
		algoSettings.setInteger("size", 10000);

		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		executionsStorage.addEodExecution(new EodExecution("e1", TestingEodAlgorithm.class.getName(), algoSettings));

		final TradeProcessorInit settings = new TradeProcessorInit(ss, period, executionsStorage);

		final TradeProcessor tradeProcessor = new TradeProcessor(settings);
		tradeProcessor.simulate(period);

		final ExecutionStarter es = tradeProcessor.getExecutionStorage();
		assertEquals(1, es.getEodAlgorithmsSize());

		final TestingEodAlgorithm ta = (TestingEodAlgorithm) es.getEodAlgorithm("e1");
		assertEquals(6, ta.datafeeds.size());

		int[] expectedDatafeedSizes = { 1, 1, 2, 2, 3, 2 };

		for (int i = 0; i < expectedDatafeedSizes.length; ++i)
			assertEquals(expectedDatafeedSizes[i], ta.datafeeds.get(i).size());

		final SignalsStorage signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		final EodSignal e1s1 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 30).toDate()).getSignal(EodSignal.class);
		assertTrue(e1s1.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-10-30", ((TestingEodAlgorithmSignal) e1s1).dateRepresentation);

		final EodSignal e1s2 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 31).toDate()).getSignal(TestingEodAlgorithmSignal.class);
		assertTrue(e1s2.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-10-31", ((TestingEodAlgorithmSignal) e1s2).dateRepresentation);

		final EodSignal e1s3 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 01).toDate()).getSignal(TestingEodAlgorithmSignal.class);
		assertTrue(e1s3.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-11-01", ((TestingEodAlgorithmSignal) e1s3).dateRepresentation);

		final EodSignal e1s6 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 04).toDate()).getSignal(EodSignal.class);
		assertTrue(e1s6.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-11-04", ((TestingEodAlgorithmSignal) e1s6).dateRepresentation);

		assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 6).toDate()));
		assertNull(signalsStorage.getEodSignal("e2", new LocalDate(2013, 11, 3).toDate()));
		assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 29).toDate()));
	}

	public void testTradeProcessorWithStatistics() throws Exception {
		final StockStorage ss = StockStorageFactory.createStockStorage(Sets.newHashSet(new String[] { "aapl", "adm", "spy" }), "./test_data/");
		final FromToPeriod period = new FromToPeriod("02-09-2013", "06-11-2013");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final AlgorithmSettings algoSettings = new AlgorithmSettingsImpl(period);

		executionsStorage.addEodExecution(new EodExecution("e1", TestingEodAlgorithm.class.getName(), algoSettings));
		final TradeProcessorInit init = new TradeProcessorInit(ss, period, executionsStorage);
		final TradeProcessor marketSimulator = new TradeProcessor(init);
		marketSimulator.simulate(period);
	}
}
