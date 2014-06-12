package stsc.general.simulator.multistarter.genetic;

import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.ExecutionInitializer;
import stsc.general.simulator.SimulatorSettings;

public class SimulatorSettingsGeneticList {

	private final StockStorage stockStorage;
	private final FromToPeriod period;
	private final boolean finished;

	private final List<ExecutionInitializer> stockInitializers;
	private final List<ExecutionInitializer> eodInitializers;

	public SimulatorSettingsGeneticList(List<ExecutionInitializer> stockInitializers, List<ExecutionInitializer> eodInitializers, StockStorage stockStorage,
			FromToPeriod period, boolean finished) {
		super();
		this.stockInitializers = stockInitializers;
		this.eodInitializers = eodInitializers;
		this.stockStorage = stockStorage;
		this.period = period;
		this.finished = finished;
	}

	public List<ExecutionInitializer> getStockInitializers() {
		return stockInitializers;
	}

	public List<ExecutionInitializer> getEodInitializers() {
		return eodInitializers;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public boolean isFinished() {
		return finished;
	}

	public SimulatorSettings generateRandom() {

		return null;
	}
}
