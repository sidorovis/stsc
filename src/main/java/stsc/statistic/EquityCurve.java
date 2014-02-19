package stsc.statistic;

import java.util.ArrayList;

import stsc.trading.TradingLog;

public class EquityCurve {
	private final TradingLog tradingLog;
	private ArrayList<Double> moneyAmount = new ArrayList<Double>();

	public EquityCurve(TradingLog tradingLog) {
		this.tradingLog = tradingLog;
	}

}
