package stsc.common;

import java.util.Date;

public interface SignalsStorage {

	public abstract void registerStockAlgorithmSerie(String stockName, String executionName, SignalsSerie<StockSignal> serie);

	public abstract void addStockSignal(String stockName, String executionName, Date date, StockSignal signal) throws BadSignalException;

	public abstract Signal<? extends StockSignal> getStockSignal(String stockName, String executionName, Date date);

	public abstract Signal<? extends StockSignal> getStockSignal(String stockName, String executionName, int index);

	public abstract int getIndexSize(String stockName, String executionName);

	public abstract void registerEodAlgorithmSerie(String executionName, SignalsSerie<EodSignal> serie);

	public abstract void addEodSignal(String executionName, Date date, EodSignal signal) throws BadSignalException;

	public abstract Signal<? extends EodSignal> getEodSignal(String executionName, Date date);

	public abstract Signal<? extends EodSignal> getEodSignal(String executionName, int index);

	public abstract int getSignalsSize(String executionName);

}