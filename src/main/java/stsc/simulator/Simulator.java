package stsc.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.statistic.Statistics;
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

	public static Simulator fromFile(final String filePath) throws Exception {
		return new Simulator(new TradeProcessorInit(filePath));
	}

	public Statistics getStatistics() {
		return statistics;
	}

}
