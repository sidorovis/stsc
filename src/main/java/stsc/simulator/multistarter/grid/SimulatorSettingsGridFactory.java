package stsc.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.simulator.ExecutionInitializer;
import stsc.storage.StockStorage;

public class SimulatorSettingsGridFactory {

	private final List<ExecutionInitializer> stockInitializers = new ArrayList<>();
	private final List<ExecutionInitializer> eodInitializers = new ArrayList<>();

	private final StockStorage stockStorage;
	private final FromToPeriod period;
	private boolean finished;

	public SimulatorSettingsGridFactory(final StockStorage stockStorage, final FromToPeriod period) {
		this.stockStorage = stockStorage;
		this.period = period;
		this.finished = true;
	}

	public SimulatorSettingsGridFactory addStock(String eName, String aName,
			AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		synchronized (stockInitializers) {
			addInitializer(stockInitializers, new ExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	public SimulatorSettingsGridFactory addEod(String eName, String aName,
			AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		synchronized (eodInitializers) {
			addInitializer(eodInitializers, new ExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	private void addInitializer(List<ExecutionInitializer> toList, ExecutionInitializer ei) {
		if (ei.hasNext())
			finished = false;
		toList.add(ei);
	}

	public SimulatorSettingsGridList getList() {
		return new SimulatorSettingsGridList(stockStorage, period, stockInitializers, eodInitializers, finished);
	}

}
