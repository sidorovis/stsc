package stsc.simulator.multistarter;

import java.util.ArrayList;

public class MultiSimulatorSettings {

	private final ArrayList<MultiStockExecution> stockExecutions = new ArrayList<>();
	private final ArrayList<MultiEodExecution> eodExecutions = new ArrayList<>();

	public MultiSimulatorSettings() {
	}

	public MultiSimulatorSettings add(MultiStockExecution mse) {
		stockExecutions.add(mse);
		return this;
	}

	public MultiSimulatorSettings add(MultiEodExecution mee) {
		eodExecutions.add(mee);
		return this;
	}

}
