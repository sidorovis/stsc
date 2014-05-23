package stsc.yahoo.liquiditator;

import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.Stock;

public class StockFilter {

	static final int minimalDaysWithDataPerLastYear = 216;
	static final int minimalDaysWithDataPerLastMonth = 19;
	static final int minimalAverageYearVolume = 60000000;
	static final float minimalDaysPercentPerLast15Years = (float) 0.9;
	static final int lastYearsAmount = 18;
	static final int daysPerYear = 256;

	static Date today = new Date();

	private static Logger logger = LogManager.getLogger("StockFilter");

	public StockFilter() {

	}

	public StockFilter(Date testToday) {
		today = testToday;
	}

	public boolean testLastPeriods(Stock s) {

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
			return false;
		}

		int monthAgoIndex = s.findDayIndex(todayDate.plusMonths(-1).toDate());
		int daysWithDataForLastMonth = days.size() - monthAgoIndex;
		if (daysWithDataForLastMonth < minimalDaysWithDataPerLastMonth) {
			logger.debug("stock " + s.getName() + " have only " + daysWithDataForLastMonth + " days for last month");
			return false;
		} else
			logger.info("stock " + s.getName() + " have " + daysWithDataForLastMonth + " days for last month");
		double volumeAmount = 0;
		for (int i = daysWithDataForLastYear; i < days.size(); ++i)
			volumeAmount += days.get(i).volume;
		volumeAmount = volumeAmount / daysWithDataForLastYear;

		if (volumeAmount < minimalAverageYearVolume) {
			logger.debug("stock " + s.getName() + " have only " + volumeAmount
					+ ", it is too small average volume amount for last year");
			return false;
		}

		return true;
	}

	private boolean testLastNYears(Stock s) {
		LocalDate todayDate = new LocalDate(today);

		ArrayList<Day> days = s.getDays();

		int tenYearsAgoIndex = s.findDayIndex(todayDate.plusYears(-lastYearsAmount).toDate());

		int realDaysForTenYears = days.size() - tenYearsAgoIndex;
		int expectedDaysForLast10Year = daysPerYear * lastYearsAmount;

		float averagePercentDaysPer10Year = (float) realDaysForTenYears / expectedDaysForLast10Year;

		if (averagePercentDaysPer10Year < minimalDaysPercentPerLast15Years) {
			logger.debug("stock " + s.getName() + " have only " + realDaysForTenYears + " days per last "
					+ lastYearsAmount + " years, thats not enought");
			return false;
		}
		return true;
	}

	public boolean test(Stock s) {
		if (s != null) {
			if (!testLastPeriods(s))
				return false;
			if (!testLastNYears(s))
				return false;
			return true;
		}
		return false;
	}
}