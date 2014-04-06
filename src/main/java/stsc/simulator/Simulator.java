package stsc.simulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.FromToPeriod;
import stsc.statistic.Statistics;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.storage.StockStorageFactory;
import stsc.trading.ExecutionsLoader;
import stsc.trading.TradeProcessor;
import stsc.trading.TradeProcessorInit;

public class Simulator {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	private final Statistics statistics;

	public Simulator(final TradeProcessorInit settings) throws Exception {
		logger.info("Simulator starting");
		final TradeProcessor tradeProcessor = new TradeProcessor(settings);
		statistics = tradeProcessor.simulate(settings.getPeriod());
		logger.info("Simulated finished");
	}

	public Simulator(final String configPath) throws Exception {
		final Properties properties = loadProperties(configPath);
		final TradeProcessorInit tps = generateTradeProcessorSettings(properties);
		logger.info("Settings readed");
		final TradeProcessor tradeProcessor = new TradeProcessor(tps);
		statistics = tradeProcessor.simulate(tps.getPeriod());
		logger.info("Simulated finished");
	}

	private Properties loadProperties(final String configPath) throws ClassNotFoundException, IOException {
		final Properties properties = new Properties();
		logger.info("Simulator starting");
		try (FileInputStream in = new FileInputStream(configPath)) {
			properties.load(in);
		}
		return properties;
	}

	private TradeProcessorInit generateTradeProcessorSettings(final Properties p) throws Exception {
		final Set<String> stockNamesSet = getStockSet(p);
		final String filterDataFolderPath = p.getProperty("Data.filter.folder");
		final StockStorage stockStorage = StockStorageFactory.createStockStorage(stockNamesSet, filterDataFolderPath);

		final String algsConfig = p.getProperty("Executions.path", "./algs.ini");
		final FromToPeriod period = new FromToPeriod(p);
		final ExecutionsLoader executionsLoader = new ExecutionsLoader(algsConfig, period);
		final ExecutionsStorage executionsStorage = executionsLoader.getExecutionsStorage();
		final TradeProcessorInit settings = new TradeProcessorInit(stockStorage, period, executionsStorage);

		return settings;
	}

	private Set<String> getStockSet(final Properties p) {
		final String[] rawStockSet = p.getProperty("Stocks").split(",");
		Set<String> stockSet = new HashSet<>();
		for (String string : rawStockSet) {
			stockSet.add(string.trim());
		}
		return stockSet;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	// public static void main(String[] args) {
	// try {
	// new Simulator("./config/simulator.ini");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
