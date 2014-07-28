package stsc.performance;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.SimulatorSettingsFactory;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.storage.AlgorithmsStorage;

class SimulatorSettingsGenerator {

	static SimulatorSettingsGridFactory getGridFactory(final StockStorage stockStorage, final List<String> openTypes, final String periodFrom,
			final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod(periodFrom, periodTo);
			final SimulatorSettingsGridFactory settings = new SimulatorSettingsGridFactory(stockStorage, period);
			fillIterator(settings, openTypes);
			return settings;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return null;
	}

	static SimulatorSettingsGeneticFactory getGeneticFactory(final StockStorage stockStorage, final List<String> openTypes,
			final String periodFrom, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod(periodFrom, periodTo);
			final SimulatorSettingsGeneticFactory settings = new SimulatorSettingsGeneticFactory(stockStorage, period);
			fillIterator(settings, openTypes);
			return settings;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return null;
	}

	private static <T> void fillIterator(SimulatorSettingsFactory<T> settings, final List<String> openTypes) throws BadParameterException,
			BadAlgorithmException {
		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryIn.add(new MpString("e", openTypes));
		settings.addStock("in", algoStockName("In"), factoryIn);

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryEma.add(new MpDouble("P", 0.1, 1.1, 0.1));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("ema", algoStockName("Ema"), factoryEma);

		final AlgorithmSettingsIteratorFactory factoryLevel = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryLevel.add(new MpDouble("f", 15.0, 20.0, 1.0));
		factoryLevel.add(new MpSubExecution("", Arrays.asList(new String[] { "ema" })));
		settings.addStock("level", algoStockName("Level"), factoryLevel);

		final AlgorithmSettingsIteratorFactory factoryOneSide = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryOneSide.add(new MpString("side", Arrays.asList(new String[] { "long", "short" })));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), factoryOneSide);

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 5));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 5));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200000.0, 5000.0));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

}
