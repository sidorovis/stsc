package stsc.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.algorithms.BadAlgorithmException;
import stsc.signals.BadSignalException;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsCalculationException;
import stsc.trading.TradeProcessor;
import stsc.trading.TradeProcessorInit;

public class Simulator {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("Simulator");

	private final Statistics statistics;

	public Simulator(final SimulatorSettings settings) throws BadAlgorithmException, StatisticsCalculationException, BadSignalException {
		logger.info("Simulator starting");
		final TradeProcessor tradeProcessor = new TradeProcessor(settings.getInit());
		statistics = tradeProcessor.simulate(settings.getInit().getPeriod());
		logger.info("Simulated finished");
	}

	public static Simulator fromFile(final String filePath) throws BadAlgorithmException, StatisticsCalculationException, BadSignalException, Exception {
		return new Simulator(new SimulatorSettings(0, new TradeProcessorInit(filePath)));
	}

	public Statistics getStatistics() {
		return statistics;
	}

}
