package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;

public class SimulatorSettingsGridCopyList extends SimulatorSettingsGridList {

	SimulatorSettingsGridCopyList(StockStorage stockStorage, FromToPeriod period, List<GridExecutionInitializer> stocks,
			List<GridExecutionInitializer> eods, boolean finished) {
		super(stockStorage, period, stocks, eods, finished);
	}

	@Override
	public Iterator<SimulatorSettings> iterator() {
		final List<GridExecutionInitializer> stocks = new ArrayList<>(stockInitializers.size());
		for (GridExecutionInitializer executionInitializer : stockInitializers) {
			stocks.add(executionInitializer.clone());
		}
		final List<GridExecutionInitializer> eods = new ArrayList<>(eodInitializers.size());
		for (GridExecutionInitializer executionInitializer : eodInitializers) {
			eods.add(executionInitializer.clone());
		}

		return new SimulatorSettingsGridIterator(stockStorage, period, stocks, eods, finished);
	}

}
