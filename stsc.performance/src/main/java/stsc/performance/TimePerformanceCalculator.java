package stsc.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.storage.AlgorithmsStorage;

class TimePerformanceCalculator {

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
			final PerformanceCalculatorSettings settings = new PerformanceCalculatorSettings();
			settings.performanceForGridTest = true;
			settings.shouldWarmUp = true;
			settings.threadsFrom = 1;
			settings.threadsTo = 4;
			settings.calculationsForAverage = 1;
			settings.printAdditionalInfo = true;

			System.out.println("Grid Search");
			settings.searcherType = SearcherType.GRID_SEARCHER;
			new PerformanceCalculator(settings).calculateTimeStatistics();
			System.out.println("Genetic Search");
			settings.searcherType = SearcherType.GENETIC_SEARCHER;
			new PerformanceCalculator(settings).calculateTimeStatistics();
			logger.debug("Performance Calculator finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
