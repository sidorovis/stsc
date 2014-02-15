package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.trading.Broker;

/**
 * @author rilley_elf Algorithms interface is an interface for all data
 *         processing algorithms
 */
public interface Algorithm {

	public void setBroker(Broker broker);

	public void process(Date date, HashMap<String, Day> datafeed);
}
