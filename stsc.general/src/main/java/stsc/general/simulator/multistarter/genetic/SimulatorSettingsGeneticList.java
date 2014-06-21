package stsc.general.simulator.multistarter.genetic;

import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionsStorage;

public class SimulatorSettingsGeneticList {

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	private final List<GeneticExecutionInitializer> stockInitializers;
	private final List<GeneticExecutionInitializer> eodInitializers;

	private Long id;

	public SimulatorSettingsGeneticList(List<GeneticExecutionInitializer> stockInitializers, List<GeneticExecutionInitializer> eodInitializers,
			StockStorage stockStorage, FromToPeriod period) {
		super();
		this.stockInitializers = stockInitializers;
		this.eodInitializers = eodInitializers;
		this.stockStorage = stockStorage;
		this.period = period;
		this.id = Long.valueOf(0);
	}

	public synchronized SimulatorSettings generateRandom() throws BadAlgorithmException {
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();

		for (GeneticExecutionInitializer i : stockInitializers) {
			final StockExecution e = new StockExecution(i.executionName, i.algorithmName, i.generateRandom(period));
			executionsStorage.addStockExecution(e);
		}
		for (GeneticExecutionInitializer i : eodInitializers) {
			final EodExecution e = new EodExecution(i.executionName, i.algorithmName, i.generateRandom(period));
			executionsStorage.addEodExecution(e);
		}
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, executionsStorage);
		final SimulatorSettings ss = new SimulatorSettings(id, init);
		id += 1;
		return ss;
	}
}
