package stsc.general.simulator.multistarter.genetic;

import java.util.concurrent.Callable;

import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;

final class SimulatorCalulatingTask implements Callable<Boolean> {

	private final StrategyGeneticSearcher strategyGeneticSearcher;
	private StrategyGeneticSearcher searcher;
	private SimulatorSettings settings;

	SimulatorCalulatingTask(StrategyGeneticSearcher strategyGeneticSearcher, StrategyGeneticSearcher searcher, SimulatorSettings settings) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
		this.searcher = searcher;
		this.settings = settings;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = false;
		try {
			final Statistics statistics = simulate();
			if (statistics != null) {
				final boolean addedToStatistics = searcher.selector.addStatistics(statistics);
				final PopulationElement populationElement = new PopulationElement(settings, statistics, addedToStatistics);
				searcher.population.add(populationElement);
				searcher.sortedPopulation.put(statistics, populationElement);
				result = true;
			}
		} finally {
			this.strategyGeneticSearcher.countDownLatch.countDown();
		}
		return result;
	}

	private Statistics simulate() {
		Simulator simulator = null;
		try {
			simulator = new Simulator(settings);
		} catch (Exception e) {
			StrategyGeneticSearcher.logger.error("Error while calculating statistics: " + e.getMessage());
			return null;
		}
		return simulator.getStatistics();
	}

}