package stsc.general.simulator.multistarter;

import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;

public abstract class SimulatorSettingsFactory<SimulatorSettingsList> {

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	protected SimulatorSettingsFactory(final StockStorage stockStorage, final FromToPeriod period) {
		this.stockStorage = stockStorage;
		this.period = period;
	}

	public AlgorithmSettingsIteratorFactory createAlgorithmSettingsFactory() {
		return new AlgorithmSettingsIteratorFactory(period);
	}

	public abstract SimulatorSettingsFactory<SimulatorSettingsList> addStock(String eName, String aName, AlgorithmSettingsIteratorFactory factory);

	public abstract SimulatorSettingsFactory<SimulatorSettingsList> addEod(String eName, String aName, AlgorithmSettingsIteratorFactory factory);

	public abstract SimulatorSettingsFactory<SimulatorSettingsList> addStock(String eName, String aName, String pName, List<String> values)
			throws BadParameterException;

	public abstract SimulatorSettingsFactory<SimulatorSettingsList> addEod(String eName, String aName, String pName, List<String> values)
			throws BadParameterException;

	public abstract SimulatorSettingsList getList();

	public abstract long size();

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

}
