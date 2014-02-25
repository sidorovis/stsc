package stsc.statistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import stsc.common.Day;
import stsc.common.DayComparator;
import stsc.common.StockInterface;
import stsc.storage.StockStorage;
import stsc.trading.TradingLog;
import stsc.trading.TradingRecord;
import stsc.trading.TradingRecord.TradingType;

public class EquityCurve {
	private class DateEquityPair {
		final Date date;
		final Double equity;

		public DateEquityPair(Date date, Double equity) {
			this.date = date;
			this.equity = equity;
		}

		public Date getDate() {
			return date;
		}

		public Double getEquity() {
			return equity;
		}
	}

	private static DayComparator dayComparator = new DayComparator();
	private final TradingLog tradingLog;
	private final StockStorage stockStorage;

	private ArrayList<DateEquityPair> moneyAmount = new ArrayList<>();

	public EquityCurve(TradingLog tradingLog, StockStorage stockStorage) {
		this.tradingLog = tradingLog;
		this.stockStorage = stockStorage;

		double maximumSpendMoneyAmount = calculateMaximumSpendMoneyAmount();
		calculateMoneyAmount(maximumSpendMoneyAmount);
	}

	private void calculateMoneyAmount(double maximumSpendMoneyAmount) {
		Date current = fillZeroDay();
		double currentMoneyAmount = maximumSpendMoneyAmount;
		final ArrayList<TradingRecord> records = tradingLog.getRecords();
// TODO fix moneyAmount calculating		
		for (TradingRecord tradingRecord : records) {
			Date date = tradingRecord.getDate();
			if (date.after(current)) {
				double equityPercent = 1 - currentMoneyAmount / maximumSpendMoneyAmount;
				moneyAmount.add(new DateEquityPair(current, equityPercent));

				currentMoneyAmount += calculateMoneyAmount(tradingRecord);
				current = date;
			} else {
				currentMoneyAmount += calculateMoneyAmount(tradingRecord);
			}
		}
//		calendar.setTime(current);
//		calendar.add(Calendar.DAY_OF_MONTH, 1);
//
//		double equityPercent = 1 - currentMoneyAmount / maximumSpendMoneyAmount;
//		moneyAmount.add(new DateEquityPair(calendar.getTime(), equityPercent));
	}
	
	private Date fillZeroDay(){
		Date current = tradingLog.getRecords().get(0).getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(current);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date preDate = calendar.getTime();
		final double maximumPercents = 100.0;
		moneyAmount.add(new DateEquityPair(preDate, maximumPercents));
		return current;
	}

	private double calculateMaximumSpendMoneyAmount() {
		ArrayList<TradingRecord> records = tradingLog.getRecords();
		double maximumSpendMoneyAmount = 0;
		double currentSpendMoneyAmount = 0;
		for (TradingRecord tradingRecord : records) {
			currentSpendMoneyAmount += calculateMoneyAmount(tradingRecord);
			if (currentSpendMoneyAmount > maximumSpendMoneyAmount)
				maximumSpendMoneyAmount = currentSpendMoneyAmount;
		}
		return maximumSpendMoneyAmount;
	}

//	private double calculateMoneyWithSideAmount(TradingRecord tradingRecord) {
//		double recordMoneyAmount = calculateRecordMoneyAmount(tradingRecord);
//		double sideMultiplyer = 1.0;
//		if (tradingRecord.getSide() == Side.SHORT)
//			sideMultiplyer = -1.0;
//		if (tradingRecord.getType() == TradingType.BUY) {
//			return sideMultiplyer * recordMoneyAmount;
//		} else {
//			return sideMultiplyer * (-recordMoneyAmount);
//		}
//
//	}

	private double calculateMoneyAmount(TradingRecord tradingRecord) {
		double recordMoneyAmount = calculateRecordMoneyAmount(tradingRecord);
		if (tradingRecord.getType() == TradingType.BUY) {
			return recordMoneyAmount;
		} else {
			return -recordMoneyAmount;
		}

	}

	private double calculateRecordMoneyAmount(TradingRecord tradingRecord) {
		Day dayData = findDay(tradingRecord);
		double openPrice = dayData.prices.getOpen();
		double recordMoneyAmount = tradingRecord.getAmount() * openPrice;
		return recordMoneyAmount;
	}

	private Day findDay(TradingRecord tradingRecord) {
		StockInterface stockInterface = stockStorage.getStock(tradingRecord.getStockName());
		ArrayList<Day> days = stockInterface.getDays();
		int index = Collections.binarySearch(days, new Day(tradingRecord.getDate()), dayComparator);

		return days.get(index);
	}

}
