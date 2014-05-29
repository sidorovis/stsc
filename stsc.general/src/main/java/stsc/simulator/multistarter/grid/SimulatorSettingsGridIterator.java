package stsc.simulator.multistarter.grid;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.storage.StockStorage;
import stsc.simulator.ExecutionInitializer;
import stsc.simulator.SimulatorSettings;
import stsc.storage.ExecutionsStorage;
import stsc.trading.TradeProcessorInit;

/*
 * This iterator could be created only one for each list, so all iterators will iterate like singleton will do
 */
public class SimulatorSettingsGridIterator implements Iterator<SimulatorSettings> {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator_settings_iterator_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("SimulatorSettingsGridIterator");

	private boolean finished;
	private final StockStorage stockStorage;
	private final FromToPeriod period;

	private final List<ExecutionInitializer> stockInitializers;
	private final List<ExecutionInitializer> eodInitializers;

	private AtomicLong ssId;

	SimulatorSettingsGridIterator(StockStorage stockStorage, FromToPeriod period, List<ExecutionInitializer> stocks, List<ExecutionInitializer> eods,
			boolean finished) {
		this.finished = finished;
		this.stockStorage = stockStorage;
		this.period = period;
		this.stockInitializers = stocks;
		this.eodInitializers = eods;
		this.ssId = new AtomicLong();
	}

	@Override
	public synchronized boolean hasNext() {
		return !finished;
	}

	@Override
	public synchronized SimulatorSettings next() {
		SimulatorSettings result = null;
		try {
			result = generateSimulatorSettings();
		} catch (BadAlgorithmException e) {
			logger.error("Problem with generating SimulatorSettings: " + e.getMessage());
			result = new SimulatorSettings(-1, new TradeProcessorInit(stockStorage, period, new ExecutionsStorage()));
			finished = true;
		}
		generateNext();
		return result;
	}

	synchronized void reset() {
		for (ExecutionInitializer i : stockInitializers) {
			i.reset();
		}
		for (ExecutionInitializer i : eodInitializers) {
			i.reset();
		}
	}

	@Override
	public synchronized void remove() {
	}

	private void generateNext() {
		int index = generateNext(stockInitializers);
		if (index == stockInitializers.size()) {
			index = generateNext(eodInitializers);
			if (index == eodInitializers.size())
				finished = true;
		}
	}

	private int generateNext(final List<ExecutionInitializer> initializers) {
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
		final SimulatorSettings ss = new SimulatorSettings(ssId.getAndIncrement(), init);
		return ss;
	}

}
