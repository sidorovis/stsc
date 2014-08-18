package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;

final class GenerateInitialPopulationsTask implements Runnable {

	private final StrategyGeneticSearcher strategyGeneticSearcher;
	private StrategyGeneticSearcher searcher;

	GenerateInitialPopulationsTask(StrategyGeneticSearcher strategyGeneticSearcher, StrategyGeneticSearcher searcher) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
		this.searcher = searcher;
	}

	@Override
	public void run() {
		for (int i = 0; i < this.strategyGeneticSearcher.settings.populationSize; ++i) {
			try {
				final SimulatorSettings ss = searcher.getRandomSettings();
				final SimulatorCalulatingTask task = new SimulatorCalulatingTask(this.strategyGeneticSearcher, searcher, ss);
				this.strategyGeneticSearcher.executor.submit(task);
			} catch (BadAlgorithmException e) {
				StrategyGeneticSearcher.logger.error("Problem while generating random simulator settings: " + e.getMessage());
			}
		}
	}
}