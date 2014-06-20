package stsc.general.simulator.multistarter.genetic;

public class GeneticExecutionInitializer {
	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGeneticList algorithmSettings;

	public GeneticExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGeneticList algorithmSettings) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.algorithmSettings = algorithmSettings;
	}

	@Override
	public String toString() {
		return executionName + "(" + algorithmName + ")\n" + algorithmSettings + "\n";
	}

}
