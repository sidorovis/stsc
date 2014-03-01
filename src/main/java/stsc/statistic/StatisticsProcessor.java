package stsc.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import stsc.common.Day;
import stsc.statistic.Statistics.StatisticsInit;
import stsc.trading.TradingLog;
import stsc.trading.TradingRecord;

public class StatisticsProcessor {

	public final static double EPSILON = 0.000001;
	private final static double PERCENTS = 100.0;

	private class Positions {
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

	public static boolean isDoubleEqual(double l, double r) {
		return (Math.abs(l - r) < EPSILON);
	}

	private class EquityCalculationData {

		private Date lastDate;
		private HashMap<String, Double> lastPrice = new HashMap<>();
		private ArrayList<TradingRecord> tradingRecords;
		private int tradingRecordsIndex = 0;

		private double spentLongCash = 0;
		private double spentShortCash = 0;

		private Positions longPositions = new Positions();
		private Positions shortPositions = new Positions();

		private double maximumSpentMoney = 0.0;

		StatisticsInit statisticsInit = Statistics.getInit();

		public EquityCalculationData(TradingLog tradingLog) {
			this.tradingRecords = tradingLog.getRecords();
		}

		public void setStockDay(String stockName, Day stockDay) {
			lastDate = stockDay.date;
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
						double oldPrice = longPositions.sharePrice(stockName);
						double priceDiff = shares * (price - oldPrice);
						addPositionClose(priceDiff);
						spentLongCash -= sharesPrice;
						longPositions.decrement(stockName, shares, sharesPrice);
					} else {
						double oldPrice = shortPositions.sharePrice(stockName);
						double priceDiff = shares * (oldPrice - price);
						addPositionClose(priceDiff);
						spentShortCash -= (sharesPrice + 2 * priceDiff);
						shortPositions.decrement(stockName, shares, sharesPrice);
					}
				}
			}
			tradingRecordsIndex = tradingRecordSize;
			double dayCache = spentLongCash + spentShortCash;
			if (maximumSpentMoney < dayCache)
				maximumSpentMoney = dayCache;
			double moneyInLongs = longPositions.cost(lastPrice);
			double moneyInShorts = shortPositions.cost(lastPrice);

			statisticsInit.equityCurve.add(lastDate, dayCache - moneyInLongs - moneyInShorts);
		}

		private void addPositionClose(double moneyDiff) {
			if (moneyDiff >= 0)
				addWin(moneyDiff);
			else
				addLoss(moneyDiff);
		}

		private void addWin(double moneyDiff) {
			statisticsInit.count += 1;
			statisticsInit.winCount += 1;
			statisticsInit.winSum += moneyDiff;
		}

		private void addLoss(double moneyDiff) {
			statisticsInit.count += 1;
			statisticsInit.lossCount += 1;
			statisticsInit.lossSum += moneyDiff;
		}

		public Statistics calculate() throws StatisticsCalculationException {
			maximumSpentMoney /= PERCENTS;
			if (isDoubleEqual(maximumSpentMoney, 0.0))
				return null;
			statisticsInit.equityCurve.recalculateWithMax(maximumSpentMoney);
			return new Statistics(statisticsInit);
		}
	};

	private EquityCalculationData equityCalculationData;

	public StatisticsProcessor(TradingLog tradingLog) {
		this.equityCalculationData = new EquityCalculationData(tradingLog);
	}

	public void setStockDay(String stockName, Day stockDay) {
		equityCalculationData.setStockDay(stockName, stockDay);
	}

	public void processEod() {
		equityCalculationData.processEod();
	}

	public Statistics calculate() throws StatisticsCalculationException {
		Statistics statisticsData = equityCalculationData.calculate();
		equityCalculationData = null;
		return statisticsData;
	}

}
