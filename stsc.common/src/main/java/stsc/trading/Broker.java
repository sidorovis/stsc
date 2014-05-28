package stsc.trading;

import java.util.Date;

import stsc.common.Side;
import stsc.storage.StockStorage;

public interface Broker {

	public abstract void setToday(Date today);

	public abstract StockStorage getStockStorage();

	// algorithms interface
	/**
	 * @return bought amount of actions
	 */
	public abstract int buy(String stockName, Side side, int sharesAmount);

	/**
	 * @return sold amount of actions
	 */
	public abstract int sell(String stockName, Side side, int sharesAmount);

}