package stsc.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.storage.AlgorithmsStorage;

public class GeneticParametersPerformanceCalculator {

	private static Logger logger = LogManager.getLogger("GeneticParameterPerformance");

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
			settings.searcherType = SearcherType.GENETIC_SEARCHER;
			settings.printAdditionalInfo = true;
			settings.printStarterInfo = false;

			System.out.println("Size of stocks: " + StockStorageSingleton.getInstance().getStockNames().size());
			PerformanceCalculator.calculateAmountOfSimulations(StockStorageSingleton.getInstance(), settings);

			for (int i = 50; i <= 70; i += 10)
				for (int u = 50; u <= 70; u += 10) {
					settings.maxSelectionIndex = i;
					settings.populationSize = u;
					new PerformanceCalculator(settings).calculateSmallStatistics();
				}
			logger.debug("Performance Calculator finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
