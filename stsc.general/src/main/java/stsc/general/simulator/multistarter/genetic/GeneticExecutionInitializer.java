package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.general.simulator.SimulatorSettings;

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

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
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

	public AlgorithmSettings mergeStock(StockExecution leftSe, StockExecution rightSe) {
		return geneticAlgorithmSettings.mergeStock(leftSe.getSettings(), rightSe.getSettings());
	}

	public AlgorithmSettings mergeEod(EodExecution leftSe, EodExecution rightSe) {
		return geneticAlgorithmSettings.mergeEod(leftSe.getSettings(), rightSe.getSettings());
	}

}
