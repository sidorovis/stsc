package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.general.simulator.SimulatorSettings;
import stsc.storage.ExecutionsStorage;

public class GeneticExecutionInitializer {
	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGeneticList geneticAlgorithmSettings;

	public GeneticExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGeneticList algorithmSettings) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.geneticAlgorithmSettings = algorithmSettings;
	}

	@Override
	public String toString() {
		return executionName + "(" + algorithmName + ")\n" + geneticAlgorithmSettings + "\n";
	}

	public AlgorithmSettings generateRandom() {
		return geneticAlgorithmSettings.generateRandom();
	}

	public void mutateStock(int mutateSettingIndex, SimulatorSettings copy) {
		final StockExecution execution = copy.getInit().getExecutionsStorage().getStockExecutions().get(mutateSettingIndex);
		final AlgorithmSettings algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	public void mutateEod(int eodIndex, SimulatorSettings copy) {
		final EodExecution execution = copy.getInit().getExecutionsStorage().getEodExecutions().get(eodIndex);
		final AlgorithmSettings algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	private void mutateAlgorithmSettings(final AlgorithmSettings algorithmSettings) {
		geneticAlgorithmSettings.mutate(algorithmSettings);
	}

}
