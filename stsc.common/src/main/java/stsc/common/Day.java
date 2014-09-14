package stsc.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import stsc.common.stocks.Prices;

public final class Day implements Comparable<Day> {

	private static final DateFormat df;

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		df = new SimpleDateFormat("dd-MM-yyyy");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public final Date date;
	public final Prices prices;
	public final double volume;
	public final double adj_close;

	public static Date createDate(final LocalDate date) {
		return nullableTime(date.toDate());
	}

	public static Date createDate() {
		return nullableTime(new Date());
	}

	public static Date createDate(String dateRepresentation) throws ParseException {
		return df.parse(dateRepresentation);
	}

	static public Date nullableTime(Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final Date result = cal.getTime();
		return result;
	}

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
	public String toString() {
		return "Day:" + date.toString();
	}
}