package stsc.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.storage.StockStorage;

public class Broker {

	private final TradingLog tradingLog = new TradingLog();
	private final StockStorage stockStorage;

	class Shares {
		private int amount = 0;

		public Shares(int amount) {
			this.amount = amount;
		}

		public void inc(int amount) {
			this.amount += amount;
		}

		public void dec(int amount) {
			this.amount -= amount;
		}

		public int getAmount() {
			return amount;
		}
	}

	private HashMap<String, Shares> openedLongShares = new HashMap<>();
	private HashMap<String, Shares> openedShortShares = new HashMap<>();

	private Date today;

	public Broker(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
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
			if (side == Side.LONG)
				buyLong(stockName, sharesAmount);
			else
				buyShort(stockName, sharesAmount);
			return sharesAmount;
		}
		return 0;
	}

	private void buyLong(String stockName, int sharesAmount) {
		Shares openAmount = openedLongShares.get(stockName);
		if (openAmount == null)
			openedLongShares.put(stockName, new Shares(sharesAmount));
		else
			openAmount.inc(sharesAmount);
		tradingLog.addBuyRecord(today, stockName, Side.LONG, sharesAmount);
	}

	private void buyShort(String stockName, int sharesAmount) {
		Shares openAmount = openedShortShares.get(stockName);
		if (openAmount == null)
			openedShortShares.put(stockName, new Shares(sharesAmount));
		else
			openAmount.inc(sharesAmount);
		tradingLog.addBuyRecord(today, stockName, Side.SHORT, sharesAmount);
	}

	/**
	 * @return sold amount of actions
	 */
	public int sell(String stockName, Side side, int sharesAmount) {
		if (dataExist(stockName)) {
			int selledAmount = 0;
			if (side == Side.LONG)
				selledAmount = sellLong(stockName, sharesAmount);
			else
				selledAmount = sellShort(stockName, sharesAmount);
			return selledAmount;
		}
		return 0;
	}

	private int sellLong(String stockName, int sharesAmount) {
		Shares openedShares = openedLongShares.get(stockName);
		if (openedShares == null) {
			return 0;
		} else {
			int openedSharesAmount = openedShares.getAmount();
			if (openedSharesAmount > sharesAmount) {
				openedShares.dec(sharesAmount);
				tradingLog.addSellRecord(today, stockName, Side.LONG, sharesAmount);
				return sharesAmount;
			} else if (openedSharesAmount == sharesAmount) {
				openedLongShares.remove(stockName);
				tradingLog.addSellRecord(today, stockName, Side.LONG, sharesAmount);
				return sharesAmount;
			} else {
				openedLongShares.remove(stockName);
				tradingLog.addSellRecord(today, stockName, Side.LONG, openedSharesAmount);
				return openedSharesAmount;
			}
		}
	}

	private int sellShort(String stockName, int sharesAmount) {
		Shares openedShares = openedShortShares.get(stockName);
		if (openedShares == null) {
			return 0;
		} else {
			int openedSharesAmount = openedShares.getAmount();
			if (openedSharesAmount > sharesAmount) {
				openedShares.dec(sharesAmount);
				tradingLog.addSellRecord(today, stockName, Side.SHORT, sharesAmount);
				return sharesAmount;
			} else if (openedSharesAmount == sharesAmount) {
				openedShortShares.remove(stockName);
				tradingLog.addSellRecord(today, stockName, Side.SHORT, sharesAmount);
				return sharesAmount;
			} else {
				openedShortShares.remove(stockName);
				tradingLog.addSellRecord(today, stockName, Side.SHORT, openedSharesAmount);
				return openedSharesAmount;
			}
		}
	}

	private boolean dataExist(String stockName) {
		final Stock stock = stockStorage.getStock(stockName);
		final ArrayList<Day> days = stock.getDays();
		final int index = Collections.binarySearch(days, new Day(today));
		return index >= 0 && index < days.size();
	}

	@Override
	public int hashCode() {
		return stockStorage.hashCode();
	}
}
