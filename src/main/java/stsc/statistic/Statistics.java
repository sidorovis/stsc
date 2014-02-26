package stsc.statistic;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;

public class Statistics {

	private Date today;
	private HashMap<String, Double> lastPrice = new HashMap<>();

	public Statistics() {
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public void setStockDay(String stockName, Day stockDay) {
		lastPrice.put(stockName, stockDay.getPrices().getOpen());
		
	}
}
