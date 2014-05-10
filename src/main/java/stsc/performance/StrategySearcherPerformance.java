package stsc.performance;

public class StrategySearcherPerformance {

	public static void main(String[] args) {
		try {
			final PerformanceCalculator ps = new PerformanceCalculator();
			ps.printStdOut();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
