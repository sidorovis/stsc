package stsc.general.simulator.multistarter.grid;

import java.util.Iterator;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;

public class SimulatorSettingsGridList implements Iterable<SimulatorSettings> {

	protected final List<GridExecutionInitializer> stockInitializers;
	protected final List<GridExecutionInitializer> eodInitializers;

	protected final StockStorage stockStorage;
	protected final FromToPeriod period;
	protected final boolean finished;

	SimulatorSettingsGridList(StockStorage stockStorage, FromToPeriod period, List<GridExecutionInitializer> stocks, List<GridExecutionInitializer> eods,
			boolean finished) {
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

	public boolean isFinished() {
		return finished;
	}

}
