package stsc.common.stocks;

public final class Prices {

	final double open;
	final double high;
	final double low;
	final double close;

	static Prices fromTokens(String[] tokens, double adjClose) {
		double o = Double.parseDouble(tokens[1]);
		double h = Double.parseDouble(tokens[2]);
		double l = Double.parseDouble(tokens[3]);
		double c = Double.parseDouble(tokens[4]);
		return calculatePrices(o, h, l, c, adjClose);
	}

	public static Prices calculatePrices(double open, double high, double low, double close, double adjClose) {
		if (Double.compare(close, 0.0) == 0) {
			final double diff_value = adjClose - close;
			return new Prices(diff_value + open, diff_value + high, diff_value + low, diff_value + close);
		}
		final double openF = open / close;
		final double highF = high / close;
		final double lowF = low / close;
		final double closeF = close / close;
		return new Prices(openF * adjClose, highF * adjClose, lowF * adjClose, closeF * adjClose);
		// return new Prices(open, high, low, close);
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

	@Override
	public String toString() {
		return toStringHelper(open) + " " + toStringHelper(high) + " " + toStringHelper(low) + " " + toStringHelper(close);
	}

	private String toStringHelper(double v) {
		return String.format("%3f", v);
	}
}