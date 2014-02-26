package stsc.common;

public class Prices{

	final double open;
	final double high;
	final double low;
	final double close;

	static Prices fromTokens(String[] tokens) {
		double o = Double.parseDouble(tokens[1]);
		double h = Double.parseDouble(tokens[2]);
		double l = Double.parseDouble(tokens[3]);
		double c = Double.parseDouble(tokens[4]);
		return new Prices(o, h, l, c);
	}

	public Prices(double o, double h, double l, double c) {
		open = o;
		high = h;
		low = l;
		close = c;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}
}