package stsc.performance;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import stsc.algorithms.BadAlgorithmException;
import stsc.common.FromToPeriod;
import stsc.common.UnitedFormatStock;
import stsc.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.MpDouble;
import stsc.simulator.multistarter.MpInteger;
import stsc.simulator.multistarter.MpString;
import stsc.simulator.multistarter.MpSubExecution;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;

public class SimulatorSettingsGenerator {

	static StockStorage stockStorage = null;

	public static StockStorage getStockStorage() {
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

	public static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	public static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	public static SimulatorSettingsGridList getSimulatorSettingsGridList(final StockStorage stockStorage,
			final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);

			final SimulatorSettingsGridFactory settings = new SimulatorSettingsGridFactory(stockStorage, period);
			fillIterator(settings, period, openTypes);

			return settings.getList();
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return null;
	}

	public static void fillIterator(SimulatorSettingsGridFactory settings, FromToPeriod period,
			final List<String> openTypes) throws BadParameterException, BadAlgorithmException {
		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(period);
		factoryIn.add(new MpString("e", openTypes));
		settings.addStock("in", algoStockName("In"), factoryIn.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(period);
		factoryEma.add(new MpDouble("P", 0.1, 0.6, 0.4));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("ema", algoStockName("Ema"), factoryEma.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryLevel = new AlgorithmSettingsIteratorFactory(period);
		factoryLevel.add(new MpDouble("f", 15.0, 20.0, 4.0));
		factoryLevel.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" })));
		settings.addStock("level", algoStockName("Level"), factoryLevel.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryOneSide = new AlgorithmSettingsIteratorFactory(period);
		factoryOneSide.add(new MpString("side", Arrays.asList(new String[] { "long", "short" })));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), factoryOneSide.getGridIterator());

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level", "in" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 10));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 10));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, 50000.0));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide.getGridIterator());
	}
}
