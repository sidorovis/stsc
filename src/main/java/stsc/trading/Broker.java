package stsc.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import stsc.common.Day;
import stsc.common.StockInterface;
import stsc.storage.StockStorage;

public class Broker {

	private final TradingLog tradingLog = new TradingLog();
	private final StockStorage stockStorage;

	private Date today;

	public Broker(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public TradingLog getTradingLog() {
		return tradingLog;
	}

	// algorithms interface
	/**
	 * @return bought amount of actions
	 */
	public int buy(String stockName, Side side, int sharesAmount) {
		if (dataExist(stockName)) {
			tradingLog.addBuyRecord(today, stockName, side, sharesAmount);
			return sharesAmount;
		}
		return 0;
	}

	/**
	 * @return sold amount of actions
	 */
	public int sell(String stockName, Side side, int sharesAmount) {
		if (dataExist(stockName)) {
			tradingLog.addSellRecord(today, stockName, side, sharesAmount);
			return sharesAmount;
		}
		return 0;
	}

	private boolean dataExist(String stockName) {
		StockInterface stock = stockStorage.getStock(stockName);
		ArrayList<Day> days = stock.getDays();
		int index = Collections.binarySearch(days, new Day(today));
		return index >= 0 && index < days.size();
	}
}
