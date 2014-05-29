package stsc.common.stocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import stsc.common.Day;
import stsc.common.DayComparator;

public abstract class Stock {

	public abstract String getName();

	public abstract ArrayList<Day> getDays();

	public int findDayIndex(Date date) {
		ArrayList<Day> days = getDays();
		int index = Collections.binarySearch(days, new Day(date), DayComparator.getInstance());
		if (index < 0)
			index = -index;
		return index;
	}

	@Override
	public String toString() {
		return "Stock(" + getName() + ")";
	}
}