package stsc.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.common.storage.SignalsStorage;

/**
 * @mark Thread Safe
 * 
 */

public class SignalsStorageImpl implements SignalsStorage {

	private HashMap<String, SignalsSerie<SerieSignal>> stockSignals = new HashMap<>();
	private HashMap<String, SignalsSerie<SerieSignal>> eodSignals = new HashMap<>();

	public SignalsStorageImpl() {
	}

	@Override
	public void registerStockAlgorithmSerie(String stockName, String executionName, Optional<SignalsSerie<SerieSignal>> serie) {
		if (serie.isPresent()) {
			final String key = stockAlgorithmKey(stockName, executionName);
			synchronized (stockSignals) {
				stockSignals.put(key, serie.get());
			}
		}
	}

	@Override
	public void addStockSignal(final String stockName, final String executionName, final Date date, final SerieSignal signal)
			throws BadSignalException {
		final String key = stockAlgorithmKey(stockName, executionName);
		synchronized (stockSignals) {
			stockSignals.get(key).addSignal(date, signal);
		}
	}

	@Override
	public SignalContainer<? extends SerieSignal> getStockSignal(final String stockName, final String executionName, final Date date) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<SerieSignal> ess;
		synchronized (stockSignals) {
			ess = stockSignals.get(key);
		}
		if (ess != null)
			return ess.getSignal(date);
		return SignalContainer.empty(date);
	}

	@Override
	public SignalContainer<? extends SerieSignal> getStockSignal(final String stockName, final String executionName, final int index) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<SerieSignal> ess;
		synchronized (stockSignals) {
			ess = stockSignals.get(key);
		}
		if (ess != null)
			return ess.getSignal(index);
		return SignalContainer.empty(index);
	}

	@Override
	public int getIndexSize(String stockName, String executionName) {
		final String key = stockAlgorithmKey(stockName, executionName);
		SignalsSerie<SerieSignal> ess;
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
	public void registerEodAlgorithmSerie(String executionName, SignalsSerie<SerieSignal> serie) {
		synchronized (eodSignals) {
			eodSignals.put(executionName, serie);
		}
	}

	@Override
	public void addEodSignal(final String executionName, final Date date, SerieSignal signal) throws BadSignalException {
		synchronized (eodSignals) {
			final SignalsSerie<SerieSignal> s = eodSignals.get(executionName);
			if (s != null)
				eodSignals.get(executionName).addSignal(date, signal);
			else
				throw new BadSignalException("No such execution '" + executionName + "'");
		}
	}

	@Override
	public SignalContainer<? extends SerieSignal> getEodSignal(final String executionName, final Date date) {
		SignalsSerie<SerieSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(date);
		return SignalContainer.empty(date);
	}

	@Override
	public SignalContainer<? extends SerieSignal> getEodSignal(final String executionName, final int index) {
		SignalsSerie<SerieSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(index);
		return SignalContainer.empty(index);
	}

	@Override
	public int getIndexSize(final String executionName) {
		SignalsSerie<SerieSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.size();
		return 0;
	}

}
