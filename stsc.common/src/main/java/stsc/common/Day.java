package stsc.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Day implements Comparable<Day> {

	static private DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");

	public final Date date;
	public final Prices prices;
	public final double volume;
	public final double adj_close;

	public Day(Date d) {
		date = d;
		prices = null;
		volume = 0.0;
		adj_close = 0.0;
	}

	public Day(Date d, Prices p, double v, double ac) {
		date = d;
		prices = p;
		volume = v;
		adj_close = ac;
	}

	public double getVolume() {
		return volume;
	}

	public double getAdj_close() {
		return adj_close;
	}

	public Date getDate() {
		return date;
	}

	public Prices getPrices() {
		return prices;
	}

	@Override
	public int compareTo(Day o) {
		return date.compareTo(o.date);
	}
	
	@Override
	public String toString(){
		return "Day:"+fd.format(date);
	}
}