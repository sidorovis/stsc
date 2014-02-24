package stsc.statistic;

import java.util.ArrayList;

import stsc.trading.TradingLog;
import stsc.trading.TradingLog.TradingRecord;

public class EquityCurve {
	private final TradingLog tradingLog;
	private ArrayList<Double> moneyAmount = new ArrayList<Double>();

	public EquityCurve(TradingLog tradingLog) {
		this.tradingLog = tradingLog;
		
		calculateMaximumSpendMoneyAmount();
	}
	
	private double calculateMaximumSpendMoneyAmount(){
		ArrayList<TradingRecord> records = tradingLog.getRecords();
		for (TradingRecord tradingRecord : records) {
			
		}
		return 0.0;
	}

}
