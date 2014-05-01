package stsc.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Iterator;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.StockExecution;
import stsc.common.FromToPeriod;
import stsc.simulator.ExecutionInitializer;
import stsc.simulator.SimulatorSettings;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.trading.TradeProcessorInit;

public class SimulatorSettingsGridIterator implements Iterable<SimulatorSettings>, Iterator<SimulatorSettings> {

	private final ArrayList<ExecutionInitializer> stockInitializers = new ArrayList<>();
	private final ArrayList<ExecutionInitializer> eodInitializers = new ArrayList<>();

	private boolean finished;

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	public SimulatorSettingsGridIterator(StockStorage stockStorage, FromToPeriod period) {
		this.finished = true;
		this.stockStorage = stockStorage;
		this.period = period;
	}

	public SimulatorSettingsGridIterator addStock(String eName, String aName, AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		addInitializer(stockInitializers, new ExecutionInitializer(eName, aName, multiAlgorithmSettings));
		return this;
	}

	public SimulatorSettingsGridIterator addEod(String eName, String aName, AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		addInitializer(eodInitializers, new ExecutionInitializer(eName, aName, multiAlgorithmSettings));
		return this;
	}

	private void addInitializer(ArrayList<ExecutionInitializer> toList, ExecutionInitializer ei) {
		if (ei.hasNext())
			finished = false;
		toList.add(ei);
	}

	@Override
	public boolean hasNext() {
		return !finished;
	}

	@Override
	public SimulatorSettings next() {
		SimulatorSettings result = null;
		try {
			result = generateSimulatorSettings();
		} catch (BadAlgorithmException e) {
			result = new SimulatorSettings(new TradeProcessorInit(stockStorage, period, new ExecutionsStorage()));
			finished = true;
		}
		generateNext();
		return result;
	}

	private void generateNext() {
		int index = generateNext(stockInitializers);
		if (index == stockInitializers.size()) {
			index = generateNext(eodInitializers);
			if (index == eodInitializers.size())
				finished = true;
		}
	}

	private int generateNext(final ArrayList<ExecutionInitializer> initializers) {
		int index = 0;
		while (index < initializers.size()) {
			final ExecutionInitializer ei = initializers.get(index);
			if (ei.hasNext()) {
				ei.next();
				if (ei.hasNext()) {
					return index;
				} else {
					ei.reset();
					index += 1;
				}
			} else {
				index += 1;
			}
		}
		return index;
	}

	@Override
	public void remove() {
	}

	@Override
	public Iterator<SimulatorSettings> iterator() {
		return this;
	}

	public void reset() {
		for (ExecutionInitializer i : stockInitializers) {
			i.reset();
		}
		for (ExecutionInitializer i : eodInitializers) {
			i.reset();
		}
	}

	private SimulatorSettings generateSimulatorSettings() throws BadAlgorithmException {
		ExecutionsStorage executionsStorage = new ExecutionsStorage();
		for (ExecutionInitializer i : stockInitializers) {
			final StockExecution e = new StockExecution(i.executionName, i.algorithmName, i.current());
			executionsStorage.addStockExecution(e);
		}
		for (ExecutionInitializer i : eodInitializers) {
			final EodExecution e = new EodExecution(i.executionName, i.algorithmName, i.current());
			executionsStorage.addEodExecution(e);
		}

		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, executionsStorage);
		final SimulatorSettings ss = new SimulatorSettings(init);
		return ss;
	}
}
