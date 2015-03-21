package stsc.general.testhelper;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.mocks.StockStorageMock;

public class TestGeneticSimulatorSettings {

	public static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	public static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	public static void fillFactory(SimulatorSettingsGeneticFactory settings, FromToPeriod period, final List<String> openTypes,
			double fStep, int nSide, int mSide, double psSide) throws BadParameterException, BadAlgorithmException {
		settings.addStock("in", algoStockName("Input"), "e", openTypes);
		settings.addStock("ema", algoStockName("Ema"), new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("P", 0.1, 0.6, 0.4))
				.add(new MpSubExecution("", "in")));
		settings.addStock(
				"level",
				algoStockName(".Level"),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("f", 15.0, 20.0, fStep)).add(
						new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" }))));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), "side", Arrays.asList(new String[] { "long", "short" }));

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level", "in" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, nSide));
		factoryPositionSide.add(new MpInteger("m", 1, 32, mSide));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, psSide));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	public static SimulatorSettingsGeneticList getGeneticList() {
		return getGeneticList(StockStorageMock.getStockStorage(), Arrays.asList(new String[] { "open", "high", "low", "close", "value" }),
				"31-12-2009");
	}

	public static SimulatorSettingsGeneticList getGeneticList(final StockStorage stockStorage, final List<String> openTypes,
			final String periodTo) {
		return getGeneticFactory(stockStorage, openTypes, periodTo).getList();
	}

	public static SimulatorSettingsGeneticFactory getGeneticFactory(final StockStorage stockStorage, final List<String> openTypes,
			final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
			fillFactory(factory, period, openTypes, 4.0, 10, 10, 50000.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGeneticFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}

	public static SimulatorSettingsGeneticList getBigGeneticList() {
		return getBigGeneticList(StockStorageMock.getStockStorage(),
				Arrays.asList(new String[] { "open", "high", "low", "close", "value" }), "31-12-2009");
	}

	public static SimulatorSettingsGeneticList getBigGeneticList(final StockStorage stockStorage, final List<String> openTypes,
			final String periodTo) {
		return getBigGeneticFactory(stockStorage, openTypes, periodTo).getList();
	}

	public static SimulatorSettingsGeneticFactory getBigGeneticFactory(final StockStorage stockStorage, final List<String> openTypes,
			final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
			fillFactory(factory, period, openTypes, 0.1, 1, 1, 1.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGeneticFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}
}
