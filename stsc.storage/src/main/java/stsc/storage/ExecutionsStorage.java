package stsc.storage;

import java.util.ArrayList;
import java.util.List;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.EodOutput;
import stsc.algorithms.Output;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.trading.Broker;

public class ExecutionsStorage implements Cloneable {

	private List<StockExecution> stockExecutions = new ArrayList<>();
	private List<EodExecution> eodExecutions = new ArrayList<>();

	public ExecutionsStorage() {
	}

	private ExecutionsStorage(final ExecutionsStorage cloneFrom) {
		this.stockExecutions = new ArrayList<>(cloneFrom.stockExecutions.size());
		for (StockExecution se : cloneFrom.stockExecutions) {
			this.stockExecutions.add(se.clone());
		}
		this.eodExecutions = new ArrayList<>(cloneFrom.eodExecutions.size());
		for (EodExecution ee : cloneFrom.eodExecutions) {
			this.eodExecutions.add(ee.clone());
		}
	}

	public void addStockExecution(StockExecution execution) {
		stockExecutions.add(execution);
	}

	public void addEodExecution(EodExecution execution) {
		eodExecutions.add(execution);
	}

	public ExecutionStarter initialize(Broker broker) throws BadAlgorithmException {
		return new ExecutionStarter(broker, stockExecutions, eodExecutions);
	}

	public String stringHashCode() {
		final StringBuilder sb = new StringBuilder();
		for (StockExecution se : stockExecutions) {
			se.stringHashCode(sb);
		}
		for (EodExecution ee : eodExecutions) {
			ee.stringHashCode(sb);
		}
		return sb.toString();
	}

	public List<StockExecution> getStockExecutions() {
		return stockExecutions;
	}

	public List<EodExecution> getEodExecutions() {
		return eodExecutions;
	}

	@Override
	public ExecutionsStorage clone() {
		return new ExecutionsStorage(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "StockExecutions = ";
		for (StockExecution se : stockExecutions) {
			result += se.getExecutionName();
			if (se != stockExecutions.get(stockExecutions.size() - 1))
				result += ", ";
		}
		result += "\n";
		for (StockExecution se : stockExecutions) {
			result += se.toString() + "\n";
		}
		result += "EodExecutions = ";
		for (EodExecution ee : eodExecutions) {
			result += ee.getExecutionName();
			if (ee != eodExecutions.get(eodExecutions.size() - 1))
				result += ", ";
		}
		result += "\n";
		for (EodExecution se : eodExecutions) {
			result += se.toString() + "\n";
		}
		return result;
	}

	public List<String> generateOutForStocks() {
		final ArrayList<String> names = new ArrayList<>();
		final ArrayList<StockExecution> initialList = new ArrayList<>(getStockExecutions());
		for (StockExecution stockExecution : initialList) {
			final AlgorithmSettingsImpl as = new AlgorithmSettingsImpl(stockExecution.getSettings().getPeriod());
			final String executionName = stockExecution.getExecutionName();
			as.addSubExecutionName(executionName);
			names.add(executionName);
			stockExecutions.add(new StockExecution(outNameFor(executionName), Output.class, as));
		}
		return names;
	}

	public List<String> generateOutForEods() {
		final ArrayList<String> names = new ArrayList<>();
		final ArrayList<EodExecution> initialList = new ArrayList<>(getEodExecutions());
		for (EodExecution eodExecution : initialList) {
			final AlgorithmSettingsImpl as = new AlgorithmSettingsImpl(eodExecution.getSettings().getPeriod());
			final String executionName = eodExecution.getExecutionName();
			as.addSubExecutionName(executionName);
			names.add(executionName);
			eodExecutions.add(new EodExecution(outNameFor(executionName), EodOutput.class, as));
		}
		return names;
	}

	public static String outNameFor(String name) {
		return name + Settings.algorithmStaticPostfix;
	}
}
