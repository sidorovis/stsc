package stsc.general.strategy;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;

public class TradingStrategy {
	private final SimulatorSettings simulatorSettings;
	private final Statistics statistics;

	public static TradingStrategy createTest(final Statistics statistics) {
		return new TradingStrategy(null, statistics);
	}

	public TradingStrategy(final SimulatorSettings simulatorSettings, final Statistics statistics) {
		this.simulatorSettings = simulatorSettings;
		this.statistics = statistics;
	}

	public SimulatorSettings getSettings() {
		return simulatorSettings;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public double getAvGain() {
		return statistics.getAvGain();
	}

	public String stringHashCode() {
		return simulatorSettings.stringHashCode();
	}

	@Override
	public String toString() {
		return simulatorSettings.toString() + "\n" + statistics.toString();
	}
}
