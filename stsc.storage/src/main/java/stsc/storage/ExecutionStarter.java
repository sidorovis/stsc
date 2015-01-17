package stsc.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecution;
import stsc.common.storage.SignalsStorage;
import stsc.common.trading.Broker;

public final class ExecutionStarter {

	private final SignalsStorage signalsStorage = new SignalsStorageImpl();

	private final StockNameToAlgorithms stockAlgorithms = new StockNameToAlgorithms();
	private final Map<String, EodAlgorithm> tradeAlgorithms = new HashMap<>();

	private final String hashCode;

	ExecutionStarter(Broker broker, List<StockExecution> stockExecutions, List<EodExecution> eodExecutions) throws BadAlgorithmException {
		final Set<String> stockNames = broker.getStockStorage().getStockNames();
		for (StockExecution execution : stockExecutions) {
			for (String stockName : stockNames) {
				final StockAlgorithm algo = execution.getInstance(stockName, signalsStorage);
				stockAlgorithms.addExecutionOnStock(stockName, execution.getExecutionName(), algo);
			}
		}
		for (EodExecution execution : eodExecutions) {
			final EodAlgorithm algo = execution.getInstance(broker, signalsStorage);
			tradeAlgorithms.put(execution.getExecutionName(), algo);
		}
		this.hashCode = generateHashCode(stockExecutions, eodExecutions);
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
		if (stockAlgorithms.getStockToAlgorithm().isEmpty())
			return 0;
		else
			return stockAlgorithms.getStockToAlgorithm().entrySet().iterator().next().getValue().size();
	}

	public Optional<StockAlgorithm> getStockAlgorithm(final String executionName, final String stockName) {
		final StockAlgorithms e = stockAlgorithms.getStockToAlgorithm().get(stockName);
		if (e != null)
			return Optional.ofNullable(e.getMap().get(executionName));
		return Optional.empty();
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}

	@Override
	public String toString() {
		return "Stocks: " + Integer.toString(stockAlgorithms.size()) + " EodAlgos: " + Integer.toString(tradeAlgorithms.size())
				+ " StockAlgos:" + Integer.toString(stockAlgorithms.size());
	}

	private String generateHashCode(List<StockExecution> stockExecutions, List<EodExecution> eodExecutions) {
		final StringBuilder sb = new StringBuilder();
		for (StockExecution se : stockExecutions) {
			se.stringHashCode(sb);
		}
		for (EodExecution ee : eodExecutions) {
			ee.stringHashCode(sb);
		}
		return sb.toString();
	}

	public String stringHashCode() {
		return hashCode;
	}

}
