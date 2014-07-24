package stsc.general.strategy;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;

public class Strategy {
	private final SimulatorSettings simulatorSettings;
	private final Statistics statistics;

	public static Strategy createTest(final Statistics statistics) {
		return new Strategy(null, statistics);
	}

	public Strategy(final SimulatorSettings simulatorSettings, final Statistics statistics) {
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

	public String getSettingsHashCode() {
		return simulatorSettings.stringHashCode();
	}
}
