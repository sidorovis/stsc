package stsc.trading;

import java.util.Date;

public class Broker {

	private TradingLog tradingLog = new TradingLog();

	private Date today;

	public Broker() {

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
		tradingLog.addBuyRecord(today, stockName, side, sharesAmount);
		return sharesAmount;
	}

	/**
	 * @return sold amount of actions
	 */
	public int sell(String stockName, Side side, int sharesAmount) {
		tradingLog.addSellRecord(today, stockName, side, sharesAmount);
		return sharesAmount;
	}
}
