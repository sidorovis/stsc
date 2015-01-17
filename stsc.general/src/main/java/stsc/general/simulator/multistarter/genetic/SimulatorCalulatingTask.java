package stsc.general.simulator.multistarter.genetic;

import java.util.Optional;
import java.util.concurrent.Callable;

import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;

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
			final Optional<Statistics> statistics = simulate();
			if (statistics.isPresent()) {
				final TradingStrategy strategy = new TradingStrategy(settings, statistics.get());
				final Optional<TradingStrategy> addedToStatistics = searcher.selector.addStrategy(strategy);
				if (addedToStatistics.isPresent()) {
					searcher.population.add(strategy);
					searcher.sortedPopulation.put(strategy, addedToStatistics != null);
					result = true;
				}
			}
		} finally {
			this.strategyGeneticSearcher.countDownLatch.countDown();
		}
		return result;
	}

	private Optional<Statistics> simulate() {
		Simulator simulator = null;
		try {
			simulator = new Simulator(settings);
		} catch (Exception e) {
			StrategyGeneticSearcher.logger.error("Error while calculating statistics: " + e.getMessage());
			return Optional.empty();
		}
		return Optional.of(simulator.getStatistics());
	}

}