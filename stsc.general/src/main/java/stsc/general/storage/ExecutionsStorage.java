package stsc.general.storage;

import java.util.ArrayList;
import java.util.List;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.trading.Broker;

public class ExecutionsStorage {

	private List<StockExecution> stockExecutions = new ArrayList<>();
	private List<EodExecution> eodExecutions = new ArrayList<>();

	public ExecutionsStorage() {
	}

	public void addStockExecution(StockExecution execution) throws BadAlgorithmException {
		stockExecutions.add(execution);
	}

	public void addEodExecution(EodExecution execution) throws BadAlgorithmException {
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

}
