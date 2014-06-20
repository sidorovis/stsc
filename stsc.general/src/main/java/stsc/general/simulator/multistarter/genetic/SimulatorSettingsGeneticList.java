package stsc.general.simulator.multistarter.genetic;

import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.grid.GridExecutionInitializer;

public class SimulatorSettingsGeneticList {

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	private final List<GridExecutionInitializer> stockInitializers;
	private final List<GridExecutionInitializer> eodInitializers;

	public SimulatorSettingsGeneticList(List<GridExecutionInitializer> stockInitializers, List<GridExecutionInitializer> eodInitializers, StockStorage stockStorage,
			FromToPeriod period) {
		super();
		this.stockInitializers = stockInitializers;
		this.eodInitializers = eodInitializers;
		this.stockStorage = stockStorage;
		this.period = period;
	}

	public List<GridExecutionInitializer> getStockInitializers() {
		return stockInitializers;
	}

	public List<GridExecutionInitializer> getEodInitializers() {
		return eodInitializers;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public SimulatorSettings generateRandom() {

		return null;
	}
}
