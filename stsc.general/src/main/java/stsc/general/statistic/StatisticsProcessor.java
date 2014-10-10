package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.google.common.math.DoubleMath;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.EquityCurve.Element;
import stsc.general.statistic.Statistics.StatisticsInit;
import stsc.general.trading.TradingLog;
import stsc.general.trading.TradingRecord;

public final class StatisticsProcessor {

	private final static double PERCENTS = 100.0;

	protected class Positions {
		public class Position {
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
				return this.shares == 0;
			}

			public double sharePrice() {
				return spentMoney / shares;
			}

			@Override
			public String toString() {
				return Double.toString(shares);
			}
		}

		public HashMap<String, Position> positions = new HashMap<>();

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

		public int size() {
			return positions.size();
		}

		@Override
		public String toString() {
			return "(" + Integer.toString(positions.size()) + "): " + positions.toString();
		}

	}

	private final class EquityProcessor {

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
		private ArrayList<Integer> startMonthsIndexes = new ArrayList<>();

		StatisticsInit statisticsInit = Statistics.createInit();

		EquityProcessor(TradingLog tradingLog) {
			this.tradingRecords = tradingLog.getRecords();
		}

		void setStockDay(String stockName, Day stockDay) {
			lastDate = stockDay.date;
			lastPrice.put(stockName, stockDay.getPrices().getOpen());
		}

		double processEod(boolean debug) {
			final int tradingRecordSize = tradingRecords.size();
			for (int i = tradingRecordsIndex; i < tradingRecordSize; ++i) {
				final TradingRecord record = tradingRecords.get(i);
				final String stockName = record.getStockName();

				final double price = lastPrice.get(stockName);
				final int shares = record.getAmount();
				final double sharesPrice = shares * price;

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
						processLong(stockName, shares, price, sharesPrice);
					} else {
						processShort(stockName, shares, price, sharesPrice);
					}
				}
			}
			tradingRecordsIndex = tradingRecordSize;

			calculateMaximumSpentMoney();
			final double dayResult = calculateDayCash();
			statisticsInit.equityCurve.add(lastDate, dayResult);
			return dayResult;
		}

		private void processLong(String stockName, int shares, double price, double sharesPrice) {
			double oldPrice = longPositions.sharePrice(stockName);
			double priceDiff = shares * (price - oldPrice);
			addPositionClose(priceDiff);
			spentLongCash -= sharesPrice;
			longPositions.decrement(stockName, shares, sharesPrice);
		}

		private void processShort(String stockName, int shares, double price, double sharesPrice) {
			final double oldPrice = shortPositions.sharePrice(stockName);
			final double priceDiff = shares * (oldPrice - price);
			addPositionClose(priceDiff);
			spentShortCash -= sharesPrice; // (sharesPrice + 2 * priceDiff);
			shortPositions.decrement(stockName, shares, sharesPrice);
		}

		private double calculateDayCash() {
			double moneyInLongs = longPositions.cost(lastPrice);
			double moneyInShorts = shortPositions.cost(lastPrice);
			return spentShortCash - spentLongCash + moneyInLongs - moneyInShorts;
		}

		private void calculateMaximumSpentMoney() {
			double spentCache = spentShortCash + spentLongCash;
			if (maximumSpentMoney < spentCache)
				maximumSpentMoney = spentCache;
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

		public Statistics calculate() {
			statisticsInit.period = statisticsInit.equityCurve.size();
			closeAllPositions();
			statisticsInit.copyMoneyEquityCurve();

			if (DoubleMath.fuzzyEquals(maximumSpentMoney, 0.0, Settings.doubleEpsilon))
				return new Statistics(statisticsInit);
			maximumSpentMoney /= PERCENTS;
			statisticsInit.equityCurve.recalculateWithMax(maximumSpentMoney);

			calculateEquityStatistics();
			return new Statistics(statisticsInit);
		}

		private void closeAllPositions() {
			final int MINIMAL_DAY_IN_PERIOD = 2;
			if (statisticsInit.period > MINIMAL_DAY_IN_PERIOD && (longPositions.size() > 0 || shortPositions.size() > 0)) {
				while (longPositions.size() > 0) {
					final String stockName = longPositions.positions.keySet().iterator().next();
					final Positions.Position p = longPositions.positions.get(stockName);

					double price = lastPrice.get(stockName);
					int shares = p.shares;
					double sharesPrice = shares * price;

					processLong(stockName, shares, price, sharesPrice);
				}
				while (shortPositions.size() > 0) {
					final String stockName = shortPositions.positions.keySet().iterator().next();
					final Positions.Position p = shortPositions.positions.get(stockName);

					double price = lastPrice.get(stockName);
					int shares = p.shares;
					double sharesPrice = shares * price;

					processShort(stockName, shares, price, sharesPrice);
				}
				final double cashSum = spentShortCash - spentLongCash;
				if (maximumSpentMoney < cashSum)
					maximumSpentMoney = cashSum;
				statisticsInit.equityCurve.setLast(cashSum);
			}
		}

		private void calculateEquityStatistics() {
			final int DAYS_PER_YEAR = 250;
			if (statisticsInit.period > DAYS_PER_YEAR) {
				calculateMonthsStatistics();
				collectElementsInStartMonths();
				calculateStartMonthsStatistics();
				calculate12MonthsStatistics();
			}

			calculateDrawDownStatistics();
		}

		private void calculateDrawDownStatistics() {
			final StatisticsInit init = statisticsInit;
			final int equityCurveSize = init.equityCurve.size();

			Element ddStart = init.equityCurve.get(0);
			boolean inDrawdown = false;
			double ddSize = 0.0;
			double lastValue = ddStart.value;

			int ddCount = 0;
			double ddDurationSum = 0.0;
			double ddValueSum = 0.0;

			for (int i = 1; i < equityCurveSize; ++i) {
				Element currentElement = init.equityCurve.get(i);
				if (!inDrawdown) {
					if (currentElement.value >= lastValue)
						ddStart = currentElement;
					else {
						inDrawdown = true;
						ddSize = ddStart.value - currentElement.value;
					}
				} else {
					if (currentElement.value > lastValue) {
						if (currentElement.value >= ddStart.value) {
							final int ddLength = Days.daysBetween(new LocalDate(ddStart.date), new LocalDate(currentElement.date))
									.getDays();

							ddCount += 1;
							ddDurationSum += ddLength;
							ddValueSum += ddSize;

							checkDdLengthSizeOnMax(ddSize, ddLength);

							inDrawdown = false;
							ddStart = currentElement;
							ddSize = 0.0;
						}
					} else {
						final double currentDdSize = ddStart.value - currentElement.value;
						if (ddSize < currentDdSize)
							ddSize = currentDdSize;
					}
				}
				lastValue = currentElement.value;
			}
			if (inDrawdown) {
				final int ddLength = Days.daysBetween(new LocalDate(ddStart.date), new LocalDate(init.equityCurve.getLastElement().date))
						.getDays();
				ddCount += 1;
				ddValueSum += ddSize;
				ddDurationSum += ddLength;

				checkDdLengthSizeOnMax(ddSize, ddLength);
			}

			if (ddCount != 0) {
				init.ddDurationAvGain = ddDurationSum / ddCount;
				init.ddValueAvGain = ddValueSum / ddCount;
			}
		}

		private void checkDdLengthSizeOnMax(double ddSize, int ddLength) {
			if (ddSize > statisticsInit.ddValueMax)
				statisticsInit.ddValueMax = ddSize;
			if (ddLength > statisticsInit.ddDurationMax)
				statisticsInit.ddDurationMax = ddLength;
		}

		private void collectElementsInStartMonths() {
			final StatisticsInit init = statisticsInit;

			LocalDate nextMonthBegin = new LocalDate(init.equityCurve.get(0).date).plusMonths(1).withDayOfMonth(1);
			final int firstMonthIndex = init.equityCurve.find(nextMonthBegin.toDate());

			final int REASONABLE_AMOUNT_OF_DAYS = 15;
			if (firstMonthIndex >= REASONABLE_AMOUNT_OF_DAYS) {
				startMonthsIndexes.add(0);
			}

			final LocalDate endDate = new LocalDate(init.equityCurve.getLastElement().date);

			int nextIndex = init.equityCurve.size();
			while (nextMonthBegin.isBefore(endDate)) {
				nextIndex = init.equityCurve.find(nextMonthBegin.toDate());
				startMonthsIndexes.add(nextIndex);
				nextMonthBegin = nextMonthBegin.plusMonths(1);
			}
			if (init.equityCurve.size() - nextIndex >= REASONABLE_AMOUNT_OF_DAYS) {
				startMonthsIndexes.add(init.equityCurve.size() - 1);
			}
		}

		private void calculate12MonthsStatistics() {
			final StatisticsInit init = statisticsInit;
			final int MONTHS_PER_YEAR = 12;
			final int startMonthsIndexesSize = startMonthsIndexes.size() - MONTHS_PER_YEAR;

			ArrayList<Double> rollingWindow12Month = new ArrayList<>();
			double rollingWindow12MonthSum = 0.0;

			for (int i = 0; i < startMonthsIndexesSize; ++i) {
				final double beginPeriodValue = init.equityCurve.get(startMonthsIndexes.get(i)).value;
				final double endPeriodValue = init.equityCurve.get(startMonthsIndexes.get(i + MONTHS_PER_YEAR)).value;
				final double diff = endPeriodValue - beginPeriodValue;
				rollingWindow12Month.add(diff);
				rollingWindow12MonthSum += diff;
				if (diff > init.month12Max)
					init.month12Max = diff;
				if (diff < init.month12Min)
					init.month12Min = diff;
			}
			init.month12AvGain = rollingWindow12MonthSum / rollingWindow12Month.size();
			init.month12StdDevGain = calculateStdDev(rollingWindow12MonthSum, rollingWindow12Month);
		}

		private void calculateStartMonthsStatistics() {
			final StatisticsInit init = statisticsInit;
			final int startMonthsIndexesSize = startMonthsIndexes.size();

			double lastValue = init.equityCurve.get(0).value;
			for (int i = 1; i < startMonthsIndexesSize; ++i) {
				double nextValue = init.equityCurve.get(startMonthsIndexes.get(i)).value;
				double differentForMonth = nextValue - lastValue;
				processMonthInStartMonths(differentForMonth);
				lastValue = nextValue;
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
			final StatisticsInit init = statisticsInit;

			int index = 0;

			LocalDate indexDate = new LocalDate(init.equityCurve.get(index).date);
			LocalDate monthAgo = indexDate.plusMonths(1);

			double indexValue = init.equityCurve.get(index).value;

			double monthsCapitalsSum = 0.0;
			final ArrayList<Double> monthsDifferents = new ArrayList<>();

			final LocalDate endDate = new LocalDate(init.equityCurve.getLastElement().date);

			while (monthAgo.isBefore(endDate)) {
				index = init.equityCurve.find(monthAgo.toDate()) - 1;
				Element element = init.equityCurve.get(index);

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

			final double RISK_PERCENTS = 5.0;
			final double MONTHS_PER_YEAR = 12.0;
			final double sharpeAnnualReturn = (MONTHS_PER_YEAR / monthsDifferents.size()) * monthsCapitalsSum;
			final double sharpeStdDev = Math.sqrt(MONTHS_PER_YEAR) * calculateStdDev(monthsCapitalsSum, monthsDifferents);

			init.sharpeRatio = (sharpeAnnualReturn - RISK_PERCENTS) / sharpeStdDev;
		}
	};

	private EquityProcessor equityProcessor;

	public StatisticsProcessor(TradingLog tradingLog) {
		this.equityProcessor = new EquityProcessor(tradingLog);
	}

	public void setStockDay(String stockName, Day stockDay) {
		equityProcessor.setStockDay(stockName, stockDay);
	}

	public double processEod(boolean debug) {
		return equityProcessor.processEod(debug);
	}

	public double processEod() {
		return processEod(false);
	}

	public Statistics calculate() {
		Statistics statisticsData = equityProcessor.calculate();
		equityProcessor = null;
		return statisticsData;
	}

	public static double calculateStdDev(List<Double> elements) {
		double summ = 0.0;
		for (Double i : elements) {
			summ += i;
		}
		return calculateStdDev(summ, elements);
	}

	public static double calculateStdDev(double summ, List<Double> elements) {
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
