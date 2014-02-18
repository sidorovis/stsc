package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.storage.ExecutionSignal;
import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

/**
 * @author rilley_elf Algorithms interface is an interface for all data
 *         processing algorithms
 */
public interface AlgorithmInterface {

	public abstract void setBroker(Broker broker);

	public abstract void setExecutionName(String executionName);

	public abstract void setSignalsStorage(SignalsStorage signalsStorage);

	public abstract Class<? extends ExecutionSignal> registerSignalsClass();
	public abstract void process(Date date, HashMap<String, Day> datafeed);
}
