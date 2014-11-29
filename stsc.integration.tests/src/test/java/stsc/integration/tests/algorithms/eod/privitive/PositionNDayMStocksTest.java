package stsc.integration.tests.algorithms.eod.privitive;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.In;
import stsc.algorithms.eod.primitive.PositionNDayMStocks;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.trading.TradeProcessorInit;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class PositionNDayMStocksTest extends TestCase {
	public void testPositionNDayMStocksException() {
		try {
			EodAlgoInitHelper init = new EodAlgoInitHelper("eName");
			new PositionNDayMStocks(init.getInit());
			fail("PositionNDayMStocks algo ");
		} catch (Exception e) {
			assertTrue(e instanceof BadAlgorithmException);
		}
	}

	public void testPositionNDayMStocks() throws Exception {
		Statistics s = Simulator.fromFile(new File("./test_data/simulator_tests/ndays.ini")).getStatistics();
		assertNotNull(s);
		assertEquals(550.0, s.getPeriod());
		assertEquals(-21.784509, s.getAvGain(), Settings.doubleEpsilon);
	}

	private void testHelper(String side) throws BadAlgorithmException, BadSignalException, ParseException, IOException {
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-12-2013");
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/apa.uf"));
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period);

		final AlgorithmSettingsImpl in = new AlgorithmSettingsImpl(period);
		in.setString("e", "open");
		init.getExecutionsStorage().addStockExecution(new StockExecution("in", In.class, in));

		final AlgorithmSettingsImpl positionNDayMStocks = new AlgorithmSettingsImpl(period);
		positionNDayMStocks.setInteger("n", 22);
		positionNDayMStocks.setInteger("m", 2);
		positionNDayMStocks.setString("side", side);
		positionNDayMStocks.addSubExecutionName("in");
		init.getExecutionsStorage()
				.addEodExecution(new EodExecution("positionNDayMStocks", PositionNDayMStocks.class, positionNDayMStocks));

		final Simulator simulator = new Simulator(new SimulatorSettings(0, init));
		final Statistics s = simulator.getStatistics();
		assertEquals(0.247656, s.getFreq(), Settings.doubleEpsilon);
	}

	public void testStaticPositionNDayMStocks() throws ParseException, BadAlgorithmException, BadSignalException, IOException {
		testHelper("long");
		testHelper("short");
	}
}
