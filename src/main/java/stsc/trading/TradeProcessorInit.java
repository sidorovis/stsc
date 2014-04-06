package stsc.trading;

import stsc.common.FromToPeriod;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;

public class TradeProcessorInit {

	private final Broker broker;
	private final FromToPeriod period;
	private final ExecutionsStorage executionsStorage;

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period, final ExecutionsStorage executionsStorage) {
		this.broker = new Broker(stockStorage);
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	public Broker getBroker() {
		return broker;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
