package stsc.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import stsc.common.Day;
import stsc.common.StockInterface;

public class DayIterator {
	private final Date from;

	private ArrayList<Day> days;
	private int currentIterator;

	public DayIterator(StockInterface stock, Date from) {
		this.from = from;
		this.days = stock.getDays();
		reset();
	}

	public void reset() {
		if (days.size() > 0 && days.get(0).date.compareTo(from) >= 0)
			currentIterator = 0;
		else {
			currentIterator = Collections.binarySearch(days, new Day(from));
			if (currentIterator < 0) {
				if (-currentIterator >= days.size())
					currentIterator = days.size();
				else
					currentIterator = -currentIterator - 1;
			}
		}
	}

	public boolean dataFound() {
		return currentIterator < days.size();
	}

	public Day getCurrentDayAndNext(Day currentDay) {
		if (currentIterator < days.size()) {
			Day day = days.get(currentIterator);
			int dayCompare = day.compareTo(currentDay);
			if (dayCompare == 0) {
				currentIterator++;
				return day;
			} else if (dayCompare < 0) {
				currentIterator = Collections.binarySearch(days, currentDay);
				if (currentIterator < 0) {
					currentIterator = -currentIterator;
					return null;
				}
				if (currentIterator >= 0 && currentIterator < days.size())
					return days.get(currentIterator);

			} else {
				return null;
			}
			return null;
		}
		return null;
	}
}