package stsc.common.storage;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;

public interface SignalsStorage {

	public abstract void registerStockAlgorithmSerie(String stockName, String executionName, SignalsSerie<SerieSignal> serie);

	public abstract void addStockSignal(String stockName, String executionName, Date date, SerieSignal signal) throws BadSignalException;

	public abstract Signal<? extends SerieSignal> getStockSignal(String stockName, String executionName, Date date);

	public abstract Signal<? extends SerieSignal> getStockSignal(String stockName, String executionName, int index);

	public abstract int getIndexSize(String stockName, String executionName);

	public abstract void registerEodAlgorithmSerie(String executionName, SignalsSerie<SerieSignal> serie);

	public abstract void addEodSignal(String executionName, Date date, SerieSignal signal) throws BadSignalException;

	public abstract Signal<? extends SerieSignal> getEodSignal(String executionName, Date date);

	public abstract Signal<? extends SerieSignal> getEodSignal(String executionName, int index);

	public abstract int getIndexSize(String executionName);

}