package stsc.liquiditator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.Day;
import stsc.common.Stock;

public class StockFilter {

	static final int minimalDaysWithDataPerLastYear = 200;
	static final int minimalDaysWithDataPerLastMonth = 16;
	static final int minimalAverageYearVolume = 50000000;

	static Date today = new Date();

	private static Logger logger = LogManager.getLogger("StockFilter");

	class DayComparator implements Comparator<Day> {

		public int compare(Day o1, Day o2) {
			return o1.date.compareTo(o2.date);
		}
	};

	public StockFilter() {

	}

	public StockFilter(Date testToday) {
		today = testToday;
	}

	public boolean testLastPeriods(Stock s) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DAY_OF_MONTH);

		Calendar yearAgoCalendar = Calendar.getInstance();
		yearAgoCalendar.set(year - 1, month, date);

		Date yearAgo = yearAgoCalendar.getTime();

		ArrayList<Day> days = s.getDaysAsArrayList();
		int yearAgoIndex = Collections.binarySearch(days, new Day(yearAgo), new DayComparator());
		if (yearAgoIndex < 0)
			yearAgoIndex = -yearAgoIndex;
		int daysWithDataForLastYear = days.size() - yearAgoIndex;
		if (daysWithDataForLastYear < minimalDaysWithDataPerLastYear) {
			logger.debug("stock " + s.getName() + " have only " + daysWithDataForLastYear + " days for last year");
			return false;
		}

		Calendar monthAgoCalendar = Calendar.getInstance();
		monthAgoCalendar.set(year, month, date);
		monthAgoCalendar.add(Calendar.MONTH, -1);

		Date monthAgo = monthAgoCalendar.getTime();

		int monthAgoIndex = Collections.binarySearch(days, new Day(monthAgo), new DayComparator());
		if (monthAgoIndex < 0)
			monthAgoIndex = -monthAgoIndex;
		int daysWithDataForLastMonth = days.size() - monthAgoIndex;
		if (daysWithDataForLastMonth < minimalDaysWithDataPerLastMonth) {
			logger.debug("stock " + s.getName() + " have only " + daysWithDataForLastMonth + " days for last month");
			return false;
		}
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

	public boolean test(Stock s) {
		if (!testLastPeriods(s))
			return false;
		return true;
	}
}
