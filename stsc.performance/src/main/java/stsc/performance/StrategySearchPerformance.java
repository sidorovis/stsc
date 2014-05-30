package stsc.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.general.storage.AlgorithmsStorage;

public class StrategySearchPerformance {

	private static Logger logger = LogManager.getLogger("StrategySearchPerformance");

	private static void initialize() {
		try {
			AlgorithmsStorage.getInstance();
			SimulatorSettingsGenerator.StockStorageSingleton.getInstance("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logger.debug("Process started");
		initialize();
		logger.debug("Algorithms and Stocks reader");
		try {
			new PerformanceCalculator();
			logger.debug("Performance Calculator finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
