package stsc.general.simulator.multistarter.genetic;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;

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

	public AlgorithmSettings generateRandom(FromToPeriod period) {
		final AlgorithmSettingsImpl result = new AlgorithmSettingsImpl(period);
		return result;
	}

}
