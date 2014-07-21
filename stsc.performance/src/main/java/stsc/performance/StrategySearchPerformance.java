package stsc.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.storage.AlgorithmsStorage;

class StrategySearchPerformance {

	private static Logger logger = LogManager.getLogger("StrategySearchPerformance");

	private static void initialize() {
		try {
			AlgorithmsStorage.getInstance();
			StockStorageSingleton.getInstance("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logger.debug("Process started");
		initialize();
		logger.debug("Algorithms and Stocks reader");
		try {
			// System.out.println("Grid Search");
			// new PerformanceCalculator(SearcherType.GRID_SEARCHER);
			System.out.println("Genetic Search");
			new PerformanceCalculator(SearcherType.GENETIC_SEARCHER);
			logger.debug("Performance Calculator finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
