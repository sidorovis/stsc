package stsc.simulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.algorithms.AlgorithmSettings;
import stsc.common.MarketDataContext;
import stsc.common.UnitedFormatStock;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.storage.YahooFileStockStorage;
import stsc.trading.Broker;
import stsc.trading.ExecutionsLoader;
import stsc.trading.MarketSimulator;
import stsc.trading.MarketSimulatorSettings;

public class Simulator {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	final private AlgorithmsStorage algorithmsStorage;
	final private SignalsStorage signalsStorage;
	private List<String> stockNames = new ArrayList<>();
	private StockStorage stockStorage;
	private ExecutionsStorage executionsStorage;

	public Simulator(final String configFile) throws Exception {
		final Properties p = new Properties();
		logger.info("Simulator starting");
		try (FileInputStream in = new FileInputStream(configFile)) {
			p.load(in);
		}
		algorithmsStorage = new AlgorithmsStorage();
		signalsStorage = new SignalsStorage();

		final MarketSimulatorSettings settings = generateSettings(p);

		logger.info("Settings readed");

		final MarketSimulator simulator = new MarketSimulator(settings, executionsStorage, signalsStorage);
		simulator.simulate();

		logger.info("Simulated finished");

		simulator.printStatistics(p.getProperty("Statistics.file", "./logs/statistics.csv"));

		logger.info("Statistics printed");
	}

	private MarketSimulatorSettings generateSettings(final Properties p) throws Exception {
		final MarketSimulatorSettings settings = new MarketSimulatorSettings();

		final String[] stockList = p.getProperty("Stocks").split(",");
		for (String string : stockList) {
			settings.getStockList().add(string.trim());
		}
		stockNames = settings.getStockList();
		createStockStorage(p);
		settings.setStockStorage(stockStorage);
		final Broker broker = new Broker(stockStorage);

		settings.setBroker(broker);
		settings.setFrom(p.getProperty("Period.from"));
		settings.setTo(p.getProperty("Period.to"));

		final AlgorithmSettings algorithmSettings = new AlgorithmSettings();
		algorithmSettings.set("Period.from", settings.getFrom());
		algorithmSettings.set("Period.to", settings.getTo());

		String configFilePath = p.getProperty("Executions.path", "./config/algs.ini");
		final ExecutionsLoader loader = new ExecutionsLoader(configFilePath, stockNames, algorithmsStorage, broker, signalsStorage,
				algorithmSettings);
		executionsStorage = loader.getExecutionsStorage();

		return settings;
	}

	private void createStockStorage(final Properties p) throws ClassNotFoundException, IOException,
			InterruptedException {
		final int AMOUNT_TO_MULTI_THREAD_LOAD = 100;
		if (stockNames.size() > AMOUNT_TO_MULTI_THREAD_LOAD) {
			final MarketDataContext context = new MarketDataContext();
			context.dataFolder = p.getProperty("Data.folder");
			context.filteredDataFolder = p.getProperty("Data.filter.folder");
			stockStorage = new YahooFileStockStorage(context);
		} else {
			stockStorage = new ThreadSafeStockStorage();
			final String folder = p.getProperty("Data.filter.folder");
			for (String name : stockNames) {
				final String path = folder + name + ".uf";
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(path));
			}
		}
	}

//	public static void main(String[] args) {
//		try {
//			new Simulator("./config/simulator.ini");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
