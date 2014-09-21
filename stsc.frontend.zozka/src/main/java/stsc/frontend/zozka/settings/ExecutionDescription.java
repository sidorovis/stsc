package stsc.frontend.zozka.settings;

final class ExecutionDescription {

	public class ExecutionName {

	}

	public class AlgorithmName {

	}

	private final String executionName;
	private final String algorithmName;

	public ExecutionDescription(String executionName, String algorithmName) {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
	}

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	@Override
	public String toString() {
		return String.valueOf(executionName) + " (" + String.valueOf(algorithmName) + ")";
	}
}
