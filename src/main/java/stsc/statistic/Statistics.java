package stsc.statistic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import stsc.common.Day;
import stsc.trading.TradingLog;
import stsc.trading.TradingRecord;

public class Statistics {

	public static double Epsilon = 0.000001;
	
	public class Positions {
		private class Position {
			private int shares = 0;
			private double spentMoney = 0.0;

			public Position(int shares, double spentMoney) {
				super();
				this.shares = shares;
				this.spentMoney = spentMoney;
			}

			public void increment(int shares, double spentMoney) {
				this.shares += shares;
				this.spentMoney += spentMoney;
			}

			public boolean decrement(int shares, double spentMoney) {
				this.shares -= shares;
				this.spentMoney -= spentMoney;
				return this.shares == 0.0;
			}

			public double sharePrice() {
				return spentMoney / shares;
			}
		}

		private HashMap<String, Position> positions = new HashMap<>();

		void increment(String stockName, int shares, double sharesPrice) {
			Position position = positions.get(stockName);
			if (position != null)
				position.increment(shares, sharesPrice);
			else
				positions.put(stockName, new Position(shares, sharesPrice));
		}

		public void decrement(String stockName, int shares, double sharesPrice) {
			Position position = positions.get(stockName);
			if (position.decrement(shares, sharesPrice))
				positions.remove(stockName);
		}

		public double sharePrice(String stockName) {
			Position position = positions.get(stockName);
			return position.sharePrice();
		}

		public double cost(HashMap<String, Double> prices) {
			double result = 0.0;
			for (Map.Entry<String, Position> i : positions.entrySet()) {
				double price = prices.get(i.getKey());
				result += price * i.getValue().shares;
			}
			return result;
		}
	}

	private HashMap<String, Double> lastPrice = new HashMap<>();
	private ArrayList<TradingRecord> tradingRecords;
	private int tradingRecordsIndex = 0;

	private double spentLongCash = 0;
	private double spentShortCash = 0;

	private Positions longPositions = new Positions();
	private Positions shortPositions = new Positions();

	private ArrayList<Double> equityCurve = new ArrayList<>();

	public Statistics(TradingLog tradingLog) {
		this.tradingRecords = tradingLog.getRecords();
	}

	public void setStockDay(String stockName, Day stockDay) {
		lastPrice.put(stockName, stockDay.getPrices().getOpen());
	}

	public void processEod() {
		int tradingRecordSize = tradingRecords.size();
		for (int i = tradingRecordsIndex; i < tradingRecordSize; ++i) {
			TradingRecord record = tradingRecords.get(i);
			String stockName = record.getStockName();

			double price = lastPrice.get(stockName);
			int shares = record.getAmount();
			double sharesPrice = shares * price;

			if (record.isPurchase()) {
				if (record.isLong()) {
					spentLongCash += sharesPrice;
					longPositions.increment(stockName, shares, sharesPrice);
				} else {
					spentShortCash += sharesPrice;
					shortPositions.increment(stockName, shares, sharesPrice);
				}
			} else {
				if (record.isLong()) {
					spentLongCash -= sharesPrice;
					longPositions.decrement(stockName, shares, sharesPrice);
				} else {
					double oldPrice = shortPositions.sharePrice(stockName);
					double priceDiff = shares * (oldPrice - price);
					spentShortCash -= (sharesPrice + 2 * priceDiff);
					shortPositions.decrement(stockName, shares, sharesPrice);
				}
			}
		}
		tradingRecordsIndex = tradingRecordSize;
		double dayCache = spentLongCash + spentShortCash;
		double moneyInLongs = longPositions.cost(lastPrice);
		double moneyInShorts = shortPositions.cost(lastPrice);

		equityCurve.add(dayCache - moneyInLongs - moneyInShorts);
	}

	public ArrayList<Double> getEquityCurve() {
		return equityCurve;
	}

	
}
