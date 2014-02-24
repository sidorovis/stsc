package stsc.common;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Day implements Serializable, Comparable<Day> {

	private static final long serialVersionUID = -6744589336558997380L;
	private static DateFormat dateReader = new SimpleDateFormat("dd-MM-yyyy");

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
	public Day(String dateRepresentation, Prices p, double v, double ac) throws ParseException {
		date = dateReader.parse(dateRepresentation);
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
}