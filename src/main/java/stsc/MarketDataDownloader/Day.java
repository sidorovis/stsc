package stsc.MarketDataDownloader;

import java.io.Serializable;
import java.util.Date;

public class Day implements Serializable {

	private static final long serialVersionUID = -6744589336558997380L;
	final Date date;
	final Prices prices;
	final double volume;
	final double adj_close;

	Day(Date d) {
		date = d;
		prices = null;
		volume = 0.0;
		adj_close = 0.0;
	}

	Day(Date d, Prices p, double v, double ac) {
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
}