package stsc.simulator.multistarter.grid;

import java.util.Iterator;

import stsc.common.FromToPeriod;
import stsc.simulator.SimulatorSettings;
import stsc.storage.StockStorage;

public class TsSimulatorSettingsGridIterator extends SimulatorSettingsGridIterator {

	public TsSimulatorSettingsGridIterator(StockStorage stockStorage, FromToPeriod period) {
		super(stockStorage, period);
	}

	public synchronized SimulatorSettingsGridIterator addStock(String eName, String aName,
			AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		return super.addStock(eName, aName, multiAlgorithmSettings);
	}

	public synchronized SimulatorSettingsGridIterator addEod(String eName, String aName,
			AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		return super.addEod(eName, aName, multiAlgorithmSettings);
	}

	@Override
	public synchronized boolean hasNext() {
		return super.hasNext();
	}

	@Override
	public synchronized SimulatorSettings next() {
		return super.next();
	}

	@Override
	public synchronized Iterator<SimulatorSettings> iterator() {
		return super.iterator();
	}

	@Override
	public synchronized void reset() {
		super.reset();
	}
}
