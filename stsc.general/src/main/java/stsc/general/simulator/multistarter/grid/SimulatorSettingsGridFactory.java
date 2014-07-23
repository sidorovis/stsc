package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.SimulatorSettingsFactory;

public class SimulatorSettingsGridFactory extends SimulatorSettingsFactory<SimulatorSettingsGridList> {

	private List<GridExecutionInitializer> stockInitializers = new ArrayList<>();
	private List<GridExecutionInitializer> eodInitializers = new ArrayList<>();

	private boolean finished;

	public SimulatorSettingsGridFactory(final StockStorage stockStorage, final FromToPeriod period) {
		super(stockStorage, period);
		this.finished = true;
	}

	// add sub-algorithms

	public SimulatorSettingsGridFactory addStock(String eName, String aName, AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		synchronized (stockInitializers) {
			addInitializer(stockInitializers, new GridExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	@Override
	public SimulatorSettingsGridFactory addStock(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addStock(eName, aName, factory.getGridIterator());
	}

	public SimulatorSettingsGridFactory addEod(String eName, String aName, AlgorithmSettingsGridIterator multiAlgorithmSettings) {
		synchronized (eodInitializers) {
			addInitializer(eodInitializers, new GridExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	@Override
	public SimulatorSettingsGridFactory addEod(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addEod(eName, aName, factory.getGridIterator());
	}

	// add predefined algorithms

	@Override
	public SimulatorSettingsGridFactory addStock(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = createAlgorithmSettingsFactory();
		algoFactory.add(new MpString(pName, values));
		return addStock(eName, aName, algoFactory.getGridIterator());
	}

	@Override
	public SimulatorSettingsGridFactory addEod(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = createAlgorithmSettingsFactory();
		algoFactory.add(new MpString(pName, values));
		return addEod(eName, aName, algoFactory.getGridIterator());
	}

	private void addInitializer(List<GridExecutionInitializer> toList, GridExecutionInitializer ei) {
		if (ei.hasNext())
			finished = false;
		toList.add(ei);
	}

	@Override
	public SimulatorSettingsGridList getList() {
		final SimulatorSettingsGridList result = new SimulatorSettingsGridList(getStockStorage(), getPeriod(), stockInitializers, eodInitializers, finished);
		stockInitializers = new ArrayList<>();
		eodInitializers = new ArrayList<>();
		return result;
	}

	// TODO - method is experimental, see tests it is not finished
	public SimulatorSettingsGridCopyList getCopyList() {
		final SimulatorSettingsGridCopyList result = new SimulatorSettingsGridCopyList(getStockStorage(), getPeriod(), stockInitializers, eodInitializers,
				finished);
		stockInitializers = new ArrayList<>();
		eodInitializers = new ArrayList<>();
		return result;
	}

	@Override
	public long size() {
		long result = 1;
		for (GridExecutionInitializer ei : stockInitializers) {
			result *= ei.size();
		}
		for (GridExecutionInitializer ei : eodInitializers) {
			result *= ei.size();
		}
		return result;
	}

}
