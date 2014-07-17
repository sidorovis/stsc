package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpString;

public class SimulatorSettingsGeneticFactory {

	private List<GeneticExecutionInitializer> stockInitializers = new ArrayList<>();
	private List<GeneticExecutionInitializer> eodInitializers = new ArrayList<>();

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	public SimulatorSettingsGeneticFactory(final StockStorage stockStorage, final FromToPeriod period) {
		this.stockStorage = stockStorage;
		this.period = period;
	}

	// add sub-algorithms

	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, AlgorithmSettingsGeneticList multiAlgorithmSettings) {
		synchronized (stockInitializers) {
			addInitializer(stockInitializers, new GeneticExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addStock(eName, aName, factory.getGeneticList());
	}

	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, AlgorithmSettingsGeneticList multiAlgorithmSettings) {
		synchronized (eodInitializers) {
			addInitializer(eodInitializers, new GeneticExecutionInitializer(eName, aName, multiAlgorithmSettings));
		}
		return this;
	}

	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, AlgorithmSettingsIteratorFactory factory) {
		return addEod(eName, aName, factory.getGeneticList());
	}

	// add predefined algorithms

	public SimulatorSettingsGeneticFactory addStock(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = new AlgorithmSettingsIteratorFactory(period);
		algoFactory.add(new MpString(pName, values));
		return addStock(eName, aName, algoFactory.getGeneticList());
	}

	public SimulatorSettingsGeneticFactory addEod(String eName, String aName, String pName, List<String> values) throws BadParameterException {
		final AlgorithmSettingsIteratorFactory algoFactory = new AlgorithmSettingsIteratorFactory(period);
		algoFactory.add(new MpString(pName, values));
		return addEod(eName, aName, algoFactory.getGeneticList());
	}

	private void addInitializer(List<GeneticExecutionInitializer> toList, GeneticExecutionInitializer ei) {
		toList.add(ei);
	}

	public SimulatorSettingsGeneticList getList() {
		final SimulatorSettingsGeneticList result = new SimulatorSettingsGeneticList(stockStorage, period, stockInitializers, eodInitializers);
		stockInitializers = new ArrayList<>();
		eodInitializers = new ArrayList<>();
		return result;
	}

}
