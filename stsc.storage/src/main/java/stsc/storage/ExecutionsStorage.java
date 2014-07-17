package stsc.storage;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public ExecutionsStorage clone() {
		return new ExecutionsStorage(this);
	}

	public List<StockExecution> getStockExecutions() {
		return stockExecutions;
	}

	public List<EodExecution> getEodExecutions() {
		return eodExecutions;
	}

}
