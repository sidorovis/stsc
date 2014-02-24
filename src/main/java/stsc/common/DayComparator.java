package stsc.common;

import java.util.Comparator;

public class DayComparator implements Comparator<Day> {

	public int compare(Day o1, Day o2) {
		return o1.date.compareTo(o2.date);
	}
};
