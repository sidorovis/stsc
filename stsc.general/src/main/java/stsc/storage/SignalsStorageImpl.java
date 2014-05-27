package stsc.storage;

import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.EodSignal;
import stsc.signals.Signal;
import stsc.signals.SignalsSerie;
import stsc.signals.StockSignal;

/**
 * @mark Thread Safe
 * 
 */

public class SignalsStorageImpl implements SignalsStorage {

	private HashMap<String, SignalsSerie<StockSignal>> stockSignals = new HashMap<>();
	private HashMap<String, SignalsSerie<EodSignal>> eodSignals = new HashMap<>();

	public SignalsStorageImpl() {
	}

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#registerStockAlgorithmSerie(java.lang.String, java.lang.String, stsc.storage.SignalsSerie)
	 */
	@Override
	public void registerStockAlgorithmSerie(String stockName, String executionName, SignalsSerie<StockSignal> serie) {
		if (serie != null) {
			final String key = stockAlgorithmKey(stockName, executionName);
			synchronized (stockSignals) {
				stockSignals.put(key, serie);
			}
		}
	}

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#addStockSignal(java.lang.String, java.lang.String, java.util.Date, stsc.signals.StockSignal)
	 */
	@Override
	public void addStockSignal(final String stockName, final String executionName, final Date date, final StockSignal signal) throws BadSignalException {
		final String key = stockAlgorithmKey(stockName, executionName);
		synchronized (stockSignals) {
			stockSignals.get(key).addSignal(date, signal);
		}
	}

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getStockSignal(java.lang.String, java.lang.String, java.util.Date)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getStockSignal(java.lang.String, java.lang.String, int)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getIndexSize(java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#registerEodAlgorithmSerie(java.lang.String, stsc.storage.SignalsSerie)
	 */
	@Override
	public void registerEodAlgorithmSerie(String executionName, SignalsSerie<EodSignal> serie) {
		if (serie != null) {
			synchronized (eodSignals) {
				eodSignals.put(executionName, serie);
			}
		}
	}

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#addEodSignal(java.lang.String, java.util.Date, stsc.signals.EodSignal)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getEodSignal(java.lang.String, java.util.Date)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getEodSignal(java.lang.String, int)
	 */
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

	/* (non-Javadoc)
	 * @see stsc.storage.SignalsStorage#getSignalsSize(java.lang.String)
	 */
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
