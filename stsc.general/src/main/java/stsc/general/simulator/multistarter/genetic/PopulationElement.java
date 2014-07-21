package stsc.general.simulator.multistarter.genetic;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;

final class PopulationElement {
	SimulatorSettings settings;
	Statistics statistics;
	boolean addedAsBestStatistics;

	PopulationElement(SimulatorSettings settings, Statistics statistics, boolean addedAsBestStatistics) {
		super();
		this.settings = settings;
		this.statistics = statistics;
		this.addedAsBestStatistics = addedAsBestStatistics;
	}
}