package stsc.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.trading.TradingLog;
import stsc.trading.TradingRecord;

public class Statistics {

	private Date today;
	private HashMap<String, Double> lastPrice = new HashMap<>();
	private ArrayList<TradingRecord> tradingRecords;
	private int tradingRecordsIndex = 0;
	
	private double spentCash = 0;
	private ArrayList<Double> equityCurve = new ArrayList<>();

	public Statistics(TradingLog tradingLog) {
		this.tradingRecords = tradingLog.getRecords();
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public void setStockDay(String stockName, Day stockDay) {
		lastPrice.put(stockName, stockDay.getPrices().getOpen());
	}

	public void processEod() {
		int tradingRecordSize = tradingRecords.size();
		for (int i = tradingRecordsIndex; i < tradingRecordSize; ++i) {
			TradingRecord record = tradingRecords.get(i);
			if (record.isPurchase()) {
				if (record.isLong()) {
					
				}else{
					
				}
			} else {

			}
		}
	}
}
