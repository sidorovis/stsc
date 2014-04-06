package stsc.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class FromToPeriod {

	private final DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	private final Date from;
	private final Date to;

	public FromToPeriod(final Properties p) throws ParseException {
		this(p.getProperty("Period.from"), p.getProperty("Period.to"));
	}

	public FromToPeriod(final String from, final String to) throws ParseException {
		this.from = dateFormatter.parse(from);
		this.to = dateFormatter.parse(to);
	}

	public FromToPeriod(final Date from, final Date to) {
		this.from = from;
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

	@Override
	public String toString() {
		return from.toString() + " -> " + to.toString();
	}
}
