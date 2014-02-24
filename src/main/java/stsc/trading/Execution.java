package stsc.trading;

import stsc.algorithms.EodAlgorithm;

public class Execution {
	public final String executionName;
	public final String algorithmName;
	public Execution(String executionName, String algorithmName){
		this.executionName = executionName;
		this.algorithmName = algorithmName;
	}
	public Execution(String executionName, Class<? extends EodAlgorithm> algorithmType){
		this.executionName = executionName;
		this.algorithmName = algorithmType.getName();
	}
}
