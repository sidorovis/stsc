package stsc.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodExecution;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockExecution;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.trading.Broker;

public class ExecutionsStorage {

	private static class Executions {
		// execution name to stock algorithms
		private final HashMap<String, StockAlgorithm> map = new HashMap<>();
		private final ArrayList<StockAlgorithm> orderedAlgorithms = new ArrayList<>();

		void add(final String executionName, final StockAlgorithm algo) {
			orderedAlgorithms.add(algo);
			map.put(executionName, algo);
		}

		void simulate(final Day newDay) throws BadSignalException {
			for (StockAlgorithm algo : orderedAlgorithms) {
				algo.process(newDay);
			}
		}

		int size() {
			return orderedAlgorithms.size();
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

	private ArrayList<StockExecution> stockExecutions = new ArrayList<>();
	private ArrayList<EodExecution> eodExecutions = new ArrayList<>();

	private StockExecutions stockAlgorithms = new StockExecutions();
	private HashMap<String, EodAlgorithm> tradeAlgorithms = new HashMap<>();

	public ExecutionsStorage(final List<String> stockNames) throws BadAlgorithmException {
		this.stockNames = stockNames;
	}

	public void addStockExecution(StockExecution execution) throws BadAlgorithmException {
		stockExecutions.add(execution);
	}

	public void addEodExecution(EodExecution execution) throws BadAlgorithmException {
		eodExecutions.add(execution);
	}

	public void initializeExecutions(SignalsStorage signalsStorage, Broker broker) throws BadAlgorithmException {
		for (StockExecution execution : stockExecutions) {
			for (String stockName : stockNames) {
				final StockAlgorithm algo = execution.getInstance(stockName, signalsStorage);
				stockAlgorithms.addExecutionOnStock(stockName, execution.getName(), algo);
			}
		}
		for (EodExecution execution : eodExecutions) {
			final EodAlgorithm algo = execution.getInstance(broker, signalsStorage);
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

	public Set<String> getEodAlgorithmNames() {
		return tradeAlgorithms.keySet();
	}

	public int getStockAlgorithmsSize() {
		if (stockAlgorithms.stockToExecution.isEmpty())
			return 0;
		else
			return stockAlgorithms.stockToExecution.entrySet().iterator().next().getValue().size();
	}

	public StockAlgorithm getStockAlgorithm(final String executionName, final String stockName) {
		Executions e = stockAlgorithms.stockToExecution.get(stockName);
		if (e != null)
			return e.map.get(executionName);
		return null;
	}
}
