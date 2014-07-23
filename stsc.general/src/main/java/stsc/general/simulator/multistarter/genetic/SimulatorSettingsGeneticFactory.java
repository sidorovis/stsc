package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.SimulatorSettingsFactory;

public class SimulatorSettingsGeneticFactory extends SimulatorSettingsFactory<SimulatorSettingsGeneticList> {

	private List<GeneticExecutionInitializer> stockInitializers = new ArrayList<>();
	private List<GeneticExecutionInitializer> eodInitializers = new ArrayList<>();

	public SimulatorSettingsGeneticFactory(final StockStorage stockStorage, final FromToPeriod period) {
		super(stockStorage, period);
	}

	// add sub-algorithms

	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, AlgorithmSettingsGeneticList multiAlgorithmSettings) {
		synchronized (stockInitializers) {
			addInitializer(stockInitializers, new GeneticExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	@Override
	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addStock(eName, aName, factory.getGeneticList());
	}

	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, AlgorithmSettingsGeneticList multiAlgorithmSettings) {
		synchronized (eodInitializers) {
			addInitializer(eodInitializers, new GeneticExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	@Override
	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addEod(eName, aName, factory.getGeneticList());
	}

	// add predefined algorithms

	@Override
	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = createAlgorithmSettingsFactory();
		algoFactory.add(new MpString(pName, values));
		return addStock(eName, aName, algoFactory.getGeneticList());
	}

	@Override
	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = createAlgorithmSettingsFactory();
		algoFactory.add(new MpString(pName, values));
		return addEod(eName, aName, algoFactory.getGeneticList());
	}

	private void addInitializer(List<GeneticExecutionInitializer> toList, GeneticExecutionInitializer ei) {
		toList.add(ei);
	}

	@Override
	public SimulatorSettingsGeneticList getList() {
		final SimulatorSettingsGeneticList result = new SimulatorSettingsGeneticList(getStockStorage(), getPeriod(), stockInitializers, eodInitializers);
		stockInitializers = new ArrayList<>();
		eodInitializers = new ArrayList<>();
		return result;
	}

	@Override
	public long size() {
		long result = 1;
		for (GeneticExecutionInitializer ei : stockInitializers) {
			result *= ei.size();
		}
		for (GeneticExecutionInitializer ei : eodInitializers) {
			result *= ei.size();
		}
		return result;
	}

}
