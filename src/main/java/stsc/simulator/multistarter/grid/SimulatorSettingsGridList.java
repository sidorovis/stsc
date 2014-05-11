package stsc.simulator.multistarter.grid;

import java.util.Iterator;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.simulator.ExecutionInitializer;
import stsc.simulator.SimulatorSettings;
import stsc.storage.StockStorage;

public class SimulatorSettingsGridList implements Iterable<SimulatorSettings> {

	private final List<ExecutionInitializer> stockInitializers;
	private final List<ExecutionInitializer> eodInitializers;

	private final StockStorage stockStorage;
	private final FromToPeriod period;
	private final boolean finished;

	SimulatorSettingsGridList(StockStorage stockStorage, FromToPeriod period, List<ExecutionInitializer> stocks,
			List<ExecutionInitializer> eods, boolean finished) {
		this.stockStorage = stockStorage;
		this.period = period;
		this.stockInitializers = stocks;
		this.eodInitializers = eods;
		this.finished = finished;
	}

	@Override
	public Iterator<SimulatorSettings> iterator() {
		return new SimulatorSettingsGridIterator(stockStorage, period, stockInitializers, eodInitializers, finished);
	}

}
