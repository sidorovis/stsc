package stsc.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.storage.AlgorithmsStorage;

public class GeneticParameterPerformance {

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
			for (int i = 50; i <= 70; i += 10)
				for (int u = 50; u <= 70; u += 10)
					new PerformanceCalculator(SearcherType.GENETIC_SEARCHER, i, u);
			logger.debug("Performance Calculator finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
