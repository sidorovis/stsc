package stsc.distributed.hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.joda.time.LocalDate;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.storage.AlgorithmsStorage;
import cascading.flow.FlowConnector;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.pipe.SubAssembly;
import cascading.property.AppProps;

class GeneticSimulatorHadoopTask {

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	static private SimulatorSettingsGeneticFactory getFactory() throws BadParameterException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final LocalDate startOfPeriod = new LocalDate(2013, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);

		final FromToPeriod period = new FromToPeriod(startOfPeriod.toDate(), endOfPeriod.toDate());

		final SimulatorSettingsGeneticFactory settings = new SimulatorSettingsGeneticFactory(stockStorage, period);

		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryIn.add(new MpString("e", new String[] { "open", "close" }));
		settings.addStock("in", algoStockName("In"), factoryIn);

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryEma.add(new MpDouble("P", 0.1, 1.11, 0.2));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("ema", algoStockName("Ema"), factoryEma);

		final AlgorithmSettingsIteratorFactory factorySma = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factorySma.add(new MpDouble("n", 5, 15, 1));
		factorySma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("sma", algoStockName("Sma"), factorySma);

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "sma" })));
		factoryPositionSide.add(new MpInteger("n", 22, 250, 20));
		factoryPositionSide.add(new MpInteger("m", 20, 40, 2));
		factoryPositionSide.add(new MpDouble("ps", 2500.0, 50000.0, 2500.0));
		factoryPositionSide.add(new MpString("side", new String[] { "long", "short" }));

		return settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	private static class ReadDatafeed extends SubAssembly {

		private static final long serialVersionUID = -4869412462406981877L;

		ReadDatafeed(String inputName) {
			loadData();
			//
			try {
				final SimulatorSettingsGeneticFactory factory = getFactory();

			} catch (BadParameterException | BadAlgorithmException e) {
				e.printStackTrace();
			}
		}

		private void loadData() {
			try {
				AlgorithmsStorage.getInstance();
				StockStorageSingleton.getInstance("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
			} catch (BadAlgorithmException | ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	GeneticSimulatorHadoopTask(final String path, final String filteredPath) {
		Properties properties = new Properties();
		AppProps.setApplicationJarClass(properties, GeneticSimulatorHadoopTask.class);
		FlowConnector flowConnector = new HadoopFlowConnector(properties);

	}

	public static void main(String[] args) {
		new GeneticSimulatorHadoopTask("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
	}
}
