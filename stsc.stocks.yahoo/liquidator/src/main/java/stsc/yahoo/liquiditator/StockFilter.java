package stsc.yahoo.liquiditator;

import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.stocks.Stock;

// @formatter:off
/**
 * 
 * {@link StockFilter} class that provide possibility to test stock data.
 * Liquidity Test
 * consist of:
 *  - testLastPeriods add errors:
 *  1) if daysWithDataForLastYear < minimalDaysWithDataPerLastYear
 *  2) if daysWithDataForLastMonth < minimalDaysWithDataPerLastMonth
 *  3) if volumeAmount < minimalAverageYearVolume
 *  - testLastNYears add errors:
 *  1) if averagePercentDaysPer10Year < minimalDaysPercentPerLast15Years
 * 
 * Validity Test
 * 
 * 
 */
// @formatter:on
public class StockFilter {

	static final int minimalDaysWithDataPerLastYear = 216;
	static final int minimalDaysWithDataPerLastMonth = 19;
	static final int minimalAverageYearVolume = 60000000;
	static final float minimalDaysPercentPerLastSeveralYears = (float) 0.9;
	static final int lastYearsAmount = 18;
	static final int daysPerYear = 256;

	static final float valuableGapInPercents = 2.0f;

	static Date today = new Date();

	private static Logger logger = LogManager.getLogger("StockFilter");

	public StockFilter() {

	}

	public StockFilter(final Date testToday) {
		today = testToday;
	}

	// Liquidity Test

	private String testLastPeriods(Stock s) {
		String errors = "";

		ArrayList<Day> days = s.getDays();
		LocalDate todayDate = new LocalDate(today);

		if (todayDate.getDayOfWeek() == DateTimeConstants.SUNDAY)
			todayDate = todayDate.minusDays(2);
		else if (todayDate.getDayOfWeek() == DateTimeConstants.SATURDAY)
			todayDate = todayDate.minusDays(1);

		int yearAgoIndex = s.findDayIndex(todayDate.plusYears(-1).toDate());
		int daysWithDataForLastYear = days.size() - yearAgoIndex;
		if (daysWithDataForLastYear < minimalDaysWithDataPerLastYear) {
			logger.debug("stock " + s.getName() + " have only " + daysWithDataForLastYear + " days for last year");
			errors += "stock " + s.getName() + " have only " + daysWithDataForLastYear + " days for last year\n";
		}

		int monthAgoIndex = s.findDayIndex(todayDate.plusMonths(-1).toDate());
		int daysWithDataForLastMonth = days.size() - monthAgoIndex;
		if (daysWithDataForLastMonth < minimalDaysWithDataPerLastMonth) {
			logger.debug("stock " + s.getName() + " have only " + daysWithDataForLastMonth + " days for last month");
			errors += "stock " + s.getName() + " have only " + daysWithDataForLastMonth + " days for last month\n";
			return errors;
		} else
			logger.info("stock " + s.getName() + " have " + daysWithDataForLastMonth + " days for last month");
		double volumeAmount = 0;
		for (int i = daysWithDataForLastYear; i < days.size(); ++i)
			volumeAmount += days.get(i).volume;
		volumeAmount = volumeAmount / daysWithDataForLastYear;

		if (volumeAmount < minimalAverageYearVolume) {
			logger.debug("stock " + s.getName() + " have only " + volumeAmount + ", it is too small average volume amount for last year");
			errors += "stock " + s.getName() + " have only " + volumeAmount + ", it is too small average volume amount for last year\n";
		}
		return errors;
	}

	private String testLastNYears(Stock s) {
		String errors = "";

		LocalDate todayDate = new LocalDate(today);
		ArrayList<Day> days = s.getDays();

		int tenYearsAgoIndex = s.findDayIndex(todayDate.plusYears(-lastYearsAmount).toDate());
		int realDaysForTenYears = days.size() - tenYearsAgoIndex;
		int expectedDaysForLast10Year = daysPerYear * lastYearsAmount;

		float averagePercentDaysPerSeveralYear = (float) realDaysForTenYears / expectedDaysForLast10Year;

		if (averagePercentDaysPerSeveralYear < minimalDaysPercentPerLastSeveralYears) {
			logger.debug("stock " + s.getName() + " have only " + realDaysForTenYears + " days per last " + lastYearsAmount
					+ " years, thats not enought");
			errors += "stock " + s.getName() + " have only " + realDaysForTenYears + " days per last " + lastYearsAmount
					+ " years, thats not enought\n";
		}
		return errors;
	}

	/**
	 * @return null if there is no errors in liquidity test
	 */
	public String isLiquidTestWithError(Stock s) {
		if (s != null) {
			final String lastPeriodsErrors = testLastPeriods(s);
			final String lastNYearsErrors = testLastNYears(s);
			if (lastPeriodsErrors == "" && lastNYearsErrors == "") {
				return null;
			} else {
				return lastPeriodsErrors + lastNYearsErrors;
			}
		}
		return "Stock could not be null";
	}

	public boolean isLiquid(Stock s) {
		return isLiquidTestWithError(s) == null;
	}

	// Validity Test

	private String testGapsOnAdjectiveClose(Stock s) {
		final ArrayList<Day> days = s.getDays();

		final int todayIndex = s.findDayIndex(today) - 1;
		for (int i = 1; i < todayIndex; ++i) {
			final double previousAdjective = days.get(i - 1).getAdjClose();
			final double currentAdjective = days.get(i).getAdjClose();
			if (Double.compare(previousAdjective, 0.0) == 0) {
				return "Adjective Close Price could not be Zero (" + s.getName() + ":" + days.get(i - 1).getDate() + ")";
			}
			if (Double.compare(currentAdjective, 0.0) == 0) {
				return "Adjective Close Price could not be Zero (" + s.getName() + ":" + days.get(i).getDate() + ")";
			}
			if (Math.abs(1.0 - previousAdjective / currentAdjective) > valuableGapInPercents) {
				return "Adjective Close Price Gap found (" + s.getName() + ":" + days.get(i - 1).getDate() + ")";
			}
		}
		return "";
	}

	public String isValidWithError(Stock s) {
		if (s != null) {
			final String gapsOnAdjectiveClose = testGapsOnAdjectiveClose(s);
			if (gapsOnAdjectiveClose == "") {
				return null;
			} else {
				return gapsOnAdjectiveClose;
			}
		}
		return "Stock could not be null";
	}

	public boolean isValid(Stock s) {
		return isValidWithError(s) == null;
	}

}
