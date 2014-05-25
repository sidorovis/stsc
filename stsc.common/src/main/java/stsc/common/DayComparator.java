package stsc.common;

import java.util.Comparator;

public class DayComparator implements Comparator<Day> {

	private static DayComparator instance = new DayComparator();

	private DayComparator() {
		// hiding constructor
	}

	public int compare(final Day o1, final Day o2) {
		return o1.date.compareTo(o2.date);
	}

	public static DayComparator getInstance() {
		return instance;
	}
};
