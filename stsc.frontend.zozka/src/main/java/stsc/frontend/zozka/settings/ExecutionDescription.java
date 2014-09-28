package stsc.frontend.zozka.settings;

import stsc.common.FromToPeriod;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.genetic.AlgorithmSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.GeneticExecutionInitializer;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;

final class ExecutionDescription {

	private final String executionName;
	private final String algorithmName;

	private final AlgorithmParameters parameters;

	public ExecutionDescription(String executionName, String algorithmName) {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.parameters = new AlgorithmParameters();
	}

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public AlgorithmParameters getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return String.valueOf(executionName) + " (" + String.valueOf(algorithmName) + ")";
	}

	public GridExecutionInitializer createGridExecution(FromToPeriod period) {
		final AlgorithmSettingsGridIterator settings = new AlgorithmSettingsGridIterator(period, false, parameters);
		return new GridExecutionInitializer(executionName, algorithmName, settings);
	}

	public GeneticExecutionInitializer createGeneticExecution(FromToPeriod period) {
		final AlgorithmSettingsGeneticList settings = new AlgorithmSettingsGeneticList(period, parameters);
		return new GeneticExecutionInitializer(executionName, algorithmName, settings);
	}
}
