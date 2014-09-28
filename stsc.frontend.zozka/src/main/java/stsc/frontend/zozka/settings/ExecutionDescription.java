package stsc.frontend.zozka.settings;

import stsc.general.simulator.multistarter.AlgorithmParameters;

final class ExecutionDescription {

	private final String executionName;
	private final String algorithmName;
	private final String executionType;

	private final AlgorithmParameters parameters;

	public ExecutionDescription(String executionName, String algorithmName, String executionType) {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.executionType = executionType;
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
}
