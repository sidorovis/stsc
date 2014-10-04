package stsc.general.simulator;

import java.io.File;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import com.google.common.base.Joiner;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.SignalsStorage;
import stsc.general.statistic.Statistics;
import stsc.general.trading.TradeProcessor;
import stsc.general.trading.TradeProcessorInit;

public class Simulator {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("Simulator");

	private final Statistics statistics;
	private final SignalsStorage signalsStorage;
	
	public Simulator(final SimulatorSettings settings, Set<String> stockNames) throws BadAlgorithmException, BadSignalException {
		logger.info("Simulator starting on " + Joiner.on(",").join(stockNames));
		final TradeProcessor tradeProcessor = new TradeProcessor(settings.getInit());
		statistics = tradeProcessor.simulate(settings.getInit().getPeriod(), stockNames);
		signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		logger.info("Simulated finished");
	}

	public Simulator(final SimulatorSettings settings) throws BadAlgorithmException, BadSignalException {
		logger.info("Simulator starting");
		final TradeProcessor tradeProcessor = new TradeProcessor(settings.getInit());
		statistics = tradeProcessor.simulate(settings.getInit().getPeriod());
		signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		logger.info("Simulated finished");
	}

	public static Simulator fromFile(final File filePath) throws BadAlgorithmException, BadSignalException, Exception {
		return new Simulator(new SimulatorSettings(0, new TradeProcessorInit(filePath)));
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}

}
