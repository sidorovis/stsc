package stsc.storage;

import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.signals.EodSignal;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.common.storage.SignalsStorage;

/**
 * @mark Thread Safe
 * 
 */

public class SignalsStorageImpl implements SignalsStorage {

	private HashMap<String, SignalsSerie<StockSignal>> stockSignals = new HashMap<>();
	private HashMap<String, SignalsSerie<EodSignal>> eodSignals = new HashMap<>();

	public SignalsStorageImpl() {
	}

	@Override
	public void registerStockAlgorithmSerie(String stockName, String executionName, SignalsSerie<StockSignal> serie) {
		if (serie != null) {
			final String key = stockAlgorithmKey(stockName, executionName);
			synchronized (stockSignals) {
				stockSignals.put(key, serie);
			}
		}
	}

	@Override
	public void addStockSignal(final String stockName, final String executionName, final Date date, final StockSignal signal) throws BadSignalException {
		final String key = stockAlgorithmKey(stockName, executionName);
		synchronized (stockSignals) {
			stockSignals.get(key).addSignal(date, signal);
		}
	}

	@Override
	public Signal<? extends StockSignal> getStockSignal(final String stockName, final String executionName, final Date date) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<StockSignal> ess;
		synchronized (stockSignals) {
			ess = stockSignals.get(key);
		}
		if (ess != null)
			return ess.getSignal(date);
		return null;
	}

	@Override
	public Signal<? extends StockSignal> getStockSignal(final String stockName, final String executionName, final int index) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<StockSignal> ess;
		synchronized (stockSignals) {
			ess = stockSignals.get(key);
		}
		if (ess != null)
			return ess.getSignal(index);
		return null;
	}

	@Override
	public int getIndexSize(String stockName, String executionName) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<StockSignal> ess;
		synchronized (stockSignals) {
			ess = stockSignals.get(key);
		}
		if (ess != null)
			return ess.size();
		return 0;
	}

	private String stockAlgorithmKey(String stockName, String executionName) {
		return stockName + "#" + executionName;
	}

	// EOD

	@Override
	public void registerEodAlgorithmSerie(String executionName, SignalsSerie<EodSignal> serie) {
		if (serie != null) {
			synchronized (eodSignals) {
				eodSignals.put(executionName, serie);
			}
		}
	}

	@Override
	public void addEodSignal(final String executionName, final Date date, EodSignal signal) throws BadSignalException {
		synchronized (eodSignals) {
			final SignalsSerie<EodSignal> s = eodSignals.get(executionName);
			if (s != null)
				eodSignals.get(executionName).addSignal(date, signal);
			else
				throw new BadSignalException("No such execution '" + executionName + "'");
		}
	}

	@Override
	public Signal<? extends EodSignal> getEodSignal(final String executionName, final Date date) {
		SignalsSerie<EodSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(date);
		return null;
	}

	@Override
	public Signal<? extends EodSignal> getEodSignal(final String executionName, final int index) {
		SignalsSerie<EodSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(index);
		return null;
	}

	@Override
	public int getSignalsSize(final String executionName) {
		SignalsSerie<EodSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.size();
		return 0;
	}

}
