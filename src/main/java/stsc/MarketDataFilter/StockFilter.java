package stsc.MarketDataFilter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import stsc.MarketDataDownloader.Day;
import stsc.MarketDataDownloader.Stock;

public class StockFilter {

	static final int minimalDaysWithDataPerLastYear = 210;
	static final int minimalDaysWithDataPerLastMonth = 15;
	static final int minimalAverageYearVolume = 100000000;
	
	static Date today = new Date();

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
		int yearAgoIndex = Collections.binarySearch(days, new Day(yearAgo),
				new DayComparator());
		if (yearAgoIndex < 0)
			yearAgoIndex = -yearAgoIndex;
		int daysWithDataForLastYear = days.size() - yearAgoIndex;
		if (daysWithDataForLastYear < minimalDaysWithDataPerLastYear)
			return false;

		Calendar monthAgoCalendar = Calendar.getInstance();
		monthAgoCalendar.set(year, month, date);
		monthAgoCalendar.add(Calendar.MONTH, -1);

		Date monthAgo = monthAgoCalendar.getTime();

		int monthAgoIndex = Collections.binarySearch(days, new Day(monthAgo),
				new DayComparator());
		if (monthAgoIndex < 0)
			monthAgoIndex = -monthAgoIndex;
		int daysWithDataForLastMonth = days.size() - monthAgoIndex;
		if (daysWithDataForLastMonth < minimalDaysWithDataPerLastMonth)
			return false;

		double volume_amount = 0;
		for (int i = daysWithDataForLastYear; i < days.size(); ++i)
			volume_amount += days.get(i).volume;
		volume_amount = volume_amount / daysWithDataForLastYear;

		if (volume_amount < minimalAverageYearVolume)
			return false;
		
		return true;
	}

	public boolean test(Stock s) {
		if (!testLastPeriods(s))
			return false;
		return true;
	}
}
