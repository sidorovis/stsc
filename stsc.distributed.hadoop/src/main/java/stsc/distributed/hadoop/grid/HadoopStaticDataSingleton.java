package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.yahoo.YahooFileStockStorage;

class HadoopStaticDataSingleton {

	// StockStorage

	private static StockStorage stockStorage = null;

	static StockStorage getStockStorage(final String dataFolder, final String filteredDataFolder) throws ClassNotFoundException, IOException,
			InterruptedException {
		if (stockStorage == null) {
			stockStorage = new YahooFileStockStorage(dataFolder, filteredDataFolder);
		}
		return stockStorage;
	}

	static StockStorage getStockStorage() {
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

	// SimulatorSettingsGridList

	private static SimulatorSettingsGridList simulatorSettingsGridList;

	public static SimulatorSettingsGridList getGridList() {
		if (simulatorSettingsGridList != null) {
			return simulatorSettingsGridList;
		}
		try {
			FromToPeriod period = new FromToPeriod("01-01-2000", "01-01-2014");
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(getStockStorage(), period);
			fillFactory(period, factory);
			simulatorSettingsGridList = factory.getList();
			return simulatorSettingsGridList;
		} catch (ParseException | BadParameterException | BadAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void fillFactory(FromToPeriod period, SimulatorSettingsGridFactory settings) throws BadParameterException, BadAlgorithmException {
		settings.addStock("in", algoStockName("In"), "e", Arrays.asList(new String[] { "open" }));
		settings.addStock("ema", algoStockName("Ema"),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("P", 0.1, 0.6, 0.6)).add(new MpSubExecution("", "in")));
		settings.addStock(
				"level",
				algoStockName("Level"),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("f", 15.0, 20.0, 16)).add(
						new MpSubExecution("", Arrays.asList(new String[] { "ema" }))));
		// new MpSubExecution("", Arrays.asList(new String[] { "ema", "in"
		// }))));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), "side", Arrays.asList(new String[] { "long", "short" }));
		// settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), "side",
		// Arrays.asList(new String[] { "long", "short" }));

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level" })));
		// final AlgorithmSettingsIteratorFactory factoryPositionSide = new
		// AlgorithmSettingsIteratorFactory(period);
		// factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new
		// String[] { "ema", "level", "in" })));
		// factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new
		// String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 32));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 32));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, 160000.0));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	//

}