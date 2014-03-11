package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

/**
 * @author rilley_elf Algorithms interface is an interface for all eod-of-day
 *         data processing algorithms
 */
public interface EodAlgorithmInterface {

	public abstract void setExecutionName(String executionName);

	public abstract void setBroker(Broker broker);

	public abstract void setSignalsStorage(SignalsStorage signalsStorage);

	public abstract Class<? extends EodSignal> registerSignalsClass();

	public abstract void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException;
}
