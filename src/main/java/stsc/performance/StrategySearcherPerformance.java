package stsc.performance;

import stsc.algorithms.BadAlgorithmException;
import stsc.storage.AlgorithmsStorage;

public class StrategySearcherPerformance {

	private static void initialize() {
		try {
			AlgorithmsStorage.getInstance();
			SimulatorSettingsGenerator.getStockStorage();
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		initialize();
		try {
			final PerformanceCalculator ps = new PerformanceCalculator();
			ps.printStdOut();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
