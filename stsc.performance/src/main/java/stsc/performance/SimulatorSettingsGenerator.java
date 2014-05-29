package stsc.performance;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.MpDouble;
import stsc.simulator.multistarter.MpInteger;
import stsc.simulator.multistarter.MpString;
import stsc.simulator.multistarter.MpSubExecution;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.yahoo.YahooFileStockStorage;

class SimulatorSettingsGenerator {

	static class StockStorageSingleton {

		private static StockStorage stockStorage = null;

		static StockStorage getInstance(final String dataFolder, final String filteredDataFolder) throws ClassNotFoundException, IOException,
				InterruptedException {
			if (stockStorage == null) {
				stockStorage = new YahooFileStockStorage(dataFolder, filteredDataFolder);
			}
			return stockStorage;
		}

		static StockStorage getInstance() {
			if (stockStorage == null) {
				stockStorage = new ThreadSafeStockStorage();
				try {
					stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
					stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
					stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));
				} catch (IOException e) {
				}
			}
			return stockStorage;
		}
	}

	static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	static SimulatorSettingsGridList getSimulatorSettingsGridList(final StockStorage stockStorage, final List<String> openTypes, final String periodFrom,
			final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod(periodFrom, periodTo);

			final SimulatorSettingsGridFactory settings = new SimulatorSettingsGridFactory(stockStorage, period);
			fillIterator(settings, period, openTypes);

			return settings.getList();
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return null;
	}

	static void fillIterator(SimulatorSettingsGridFactory settings, FromToPeriod period, final List<String> openTypes) throws BadParameterException,
			BadAlgorithmException {
		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(period);
		factoryIn.add(new MpString("e", openTypes));
		settings.addStock("in", algoStockName("In"), factoryIn.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(period);
		factoryEma.add(new MpDouble("P", 0.1, 0.6, 0.6));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("ema", algoStockName("Ema"), factoryEma.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryLevel = new AlgorithmSettingsIteratorFactory(period);
		factoryLevel.add(new MpDouble("f", 15.0, 20.0, 5.0));
		factoryLevel.add(new MpSubExecution("", Arrays.asList(new String[] { "ema" })));
		settings.addStock("level", algoStockName("Level"), factoryLevel.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryOneSide = new AlgorithmSettingsIteratorFactory(period);
		factoryOneSide.add(new MpString("side", Arrays.asList(new String[] { "long", "short" })));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), factoryOneSide.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 32));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 32));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200000.0, 150000.0));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide.getGridIterator());
	}
}
