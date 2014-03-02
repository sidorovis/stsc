package stsc.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.statistic.EquityCurve.EquityCurveElement;
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
		private double sumOfStartMonths = 0.0;
		
		
		private ArrayList<Double> elementsInStartMonths = new ArrayList<>();

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
			if (moneyDiff > statisticsInit.maxWin)
				statisticsInit.maxWin = moneyDiff;
		}

		private void addLoss(double moneyDiff) {
			statisticsInit.count += 1;
			statisticsInit.lossCount += 1;
			statisticsInit.lossSum += moneyDiff;
			if (moneyDiff < statisticsInit.maxLoss)
				statisticsInit.maxLoss = moneyDiff;
		}

		public Statistics calculate() throws StatisticsCalculationException {
			maximumSpentMoney /= PERCENTS;
			if (isDoubleEqual(maximumSpentMoney, 0.0))
				return null;
			statisticsInit.equityCurve.recalculateWithMax(maximumSpentMoney);
			
			calculateEquityStatistics();
			
			return new Statistics(statisticsInit);
		}
		
		private void calculateEquityStatistics() {
			final int DAYS_PER_YEAR = 250;
			statisticsInit.period = statisticsInit.equityCurve.size();
			
			if (statisticsInit.period > DAYS_PER_YEAR) {
				calculateMonthsStatistics();
				calculateStartMonthsStatistics();
			}
		}

		private void calculateStartMonthsStatistics() {

			StatisticsInit init = statisticsInit;
			
			LocalDate nextMonthBegin = new LocalDate(init.equityCurve.get(0).date).plusMonths(1).withDayOfMonth(1);
			double lastValue = init.equityCurve.get(0).value;

			int firstMonthIndex = init.equityCurve.find(nextMonthBegin.toDate());

			final int REASONABLE_AMOUNT_OF_DAYS = 15;
			if (firstMonthIndex >= REASONABLE_AMOUNT_OF_DAYS) {
				EquityCurveElement element = init.equityCurve.get(firstMonthIndex);
				double nextValue = element.value;

				double differentForMonth = nextValue - lastValue;
				processMonthInStartMonths(differentForMonth);

				lastValue = nextValue;
				nextMonthBegin = nextMonthBegin.plusMonths(1);
			}

			LocalDate endDate = new LocalDate(init.equityCurve.getLastElement().date);

			int nextIndex = init.equityCurve.size();
			while (nextMonthBegin.isBefore(endDate)) {
				nextIndex = init.equityCurve.find(nextMonthBegin.toDate());
				EquityCurveElement element = init.equityCurve.get(nextIndex);
				double nextValue = element.value;
				
				double differentForMonth = nextValue - lastValue;
				processMonthInStartMonths(differentForMonth);

				lastValue = nextValue;
				nextMonthBegin = nextMonthBegin.plusMonths(1);
			}
			if (init.equityCurve.size() - nextIndex >= REASONABLE_AMOUNT_OF_DAYS) {
				double lastestValue = init.equityCurve.getLastElement().value;
				double differentForMonth = lastestValue - lastValue;
				processMonthInStartMonths(differentForMonth);
			}
			init.startMonthAvGain = sumOfStartMonths / elementsInStartMonths.size();
			init.startMonthStdDevGain = calculateStdDev(sumOfStartMonths, elementsInStartMonths);
		}

		private void processMonthInStartMonths(double moneyDiff) {
			elementsInStartMonths.add(moneyDiff);
			sumOfStartMonths += moneyDiff;
			if (moneyDiff > statisticsInit.startMonthMax)
				statisticsInit.startMonthMax = moneyDiff;
			if (moneyDiff < statisticsInit.startMonthMin)
				statisticsInit.startMonthMin = moneyDiff;
		}

		private void calculateMonthsStatistics() {
			StatisticsInit init = statisticsInit;
			
			int index = 0;

			LocalDate indexDate = new LocalDate(init.equityCurve.get(index).date);
			LocalDate monthAgo = indexDate.plusMonths(1);

			double indexValue = init.equityCurve.get(index).value;

			double monthsCapitalsSum = 0.0;
			ArrayList<Double> monthsDifferents = new ArrayList<>();

			final LocalDate endDate = new LocalDate(init.equityCurve.getLastElement().date);

			while (monthAgo.isBefore(endDate)) {
				index = init.equityCurve.find(monthAgo.toDate()) - 1;
				EquityCurveElement element = init.equityCurve.get(index);

				double lastValue = element.value;
				double differentForMonth = lastValue - indexValue;

				monthsDifferents.add(differentForMonth);
				monthsCapitalsSum += differentForMonth;

				indexValue = lastValue;
				monthAgo = monthAgo.plusMonths(1);
			}

			final int REASONABLE_AMOUNT_OF_DAYS = 13;
			if (init.equityCurve.size() - index >= REASONABLE_AMOUNT_OF_DAYS) {
				double lastValue = init.equityCurve.getLastElement().value;
				double differentForMonth = lastValue - indexValue;

				monthsDifferents.add(differentForMonth);
				monthsCapitalsSum += differentForMonth;
			}

			final double MONTH_PER_YEAR = 12.0;
			final double RISK_PERCENTS = 5.0;

			double sharpeAnnualReturn = (MONTH_PER_YEAR / monthsDifferents.size()) * monthsCapitalsSum;
			double sharpeStdDev = Math.sqrt(MONTH_PER_YEAR) * calculateStdDev(monthsCapitalsSum, monthsDifferents);

			init.sharpeRatio = (sharpeAnnualReturn - RISK_PERCENTS) / sharpeStdDev;
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

	public double calculateStdDev(List<Double> elements) {
		double summ = 0.0;
		for (Double i : elements) {
			summ += i;
		}
		return calculateStdDev(summ, elements);
	}

	public double calculateStdDev(double summ, List<Double> elements) {
		double result = 0.0;
		int size = elements.size();
		double average = summ / size;
		for (Double i : elements) {
			result += Math.pow((average - i), 2);
		}
		result = Math.sqrt(result / size);
		return result;
	}

}
