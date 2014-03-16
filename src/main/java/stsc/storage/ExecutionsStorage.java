package stsc.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.trading.Broker;

public class ExecutionsStorage {

	private static class Executions {
		// execution name to stock algorithms
		private final HashMap<String, StockAlgorithm> map = new HashMap<>();

		void add(final String executionName, final StockAlgorithm algo) {
			map.put(executionName, algo);
		}

		void simulate(final Day newDay) throws BadSignalException {
			for (Map.Entry<String, StockAlgorithm> sPair : map.entrySet()) {
				sPair.getValue().process(newDay);
			}
		}
	}

	private class StockExecutions {
		// stock name to execution map
		private HashMap<String, Executions> stockToExecution = new HashMap<>();

		void addExecutionOnStock(String stockName, String executionName, StockAlgorithm algo) {
			Executions se = stockToExecution.get(stockName);
			if (se == null) {
				se = new Executions();
				stockToExecution.put(stockName, se);
			}
			se.add(executionName, algo);
		}

		void simulate(String stockName, final Day newDay) throws BadSignalException {
			Executions e = stockToExecution.get(stockName);
			if (e != null)
				e.simulate(newDay);
		}
	}

	private List<String> stockNames;
	private StockExecutions stockAlgorithms = new StockExecutions();
	private HashMap<String, EodAlgorithm> tradeAlgorithms = new HashMap<>();

	public ExecutionsStorage(final List<String> stockNames) {
		this.stockNames = stockNames;
	}

	public ExecutionsStorage(final List<StockAlgorithmExecution> stockExecutions,
			final List<EodAlgorithmExecution> eodExecutions, final List<String> stockNames, final Broker broker,
			final SignalsStorage signals, final AlgorithmNamesStorage namesStorage) throws BadAlgorithmException {
		this(stockNames);
		generateExecutions(stockExecutions, eodExecutions, broker, signals, namesStorage);
	}

	public void generateExecutions(final List<StockAlgorithmExecution> stockExecutions,
			final List<EodAlgorithmExecution> eodExecutions, final Broker broker, final SignalsStorage signals,
			final AlgorithmNamesStorage namesStorage) throws BadAlgorithmException {
		for (StockAlgorithmExecution execution : stockExecutions) {
			for (String stockName : stockNames) {
				final StockAlgorithm algo = execution.getInstance(stockName, signals, new AlgorithmSettings(),
						namesStorage);
				stockAlgorithms.addExecutionOnStock(stockName, execution.getName(), algo);
			}
		}
		for (EodAlgorithmExecution execution : eodExecutions) {
			final EodAlgorithm algo = execution.getInstance(broker, signals, new AlgorithmSettings(), namesStorage);
			tradeAlgorithms.put(execution.getName(), algo);
		}

	}

	public void runStockAlgorithms(final String stockName, final Day stockDay) throws BadSignalException {
		stockAlgorithms.simulate(stockName, stockDay);
	}

	public void runEodAlgorithms(final Date today, final HashMap<String, Day> datafeed) throws BadSignalException {
		for (Map.Entry<String, EodAlgorithm> i : tradeAlgorithms.entrySet()) {
			i.getValue().process(today, datafeed);
		}
	}

	public int getEodAlgorithmsSize() {
		return tradeAlgorithms.size();
	}

	public EodAlgorithm getEodAlgorithm(final String key) {
		return tradeAlgorithms.get(key);
	}

	public StockAlgorithm getStockAlgorithm(final String executionName, final String stockName) {
		Executions e = stockAlgorithms.stockToExecution.get(stockName);
		if (e != null)
			return e.map.get(executionName);
		return null;
	}
}
