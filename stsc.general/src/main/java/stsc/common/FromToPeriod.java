package stsc.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class FromToPeriod implements Externalizable {

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

	public static FromToPeriod read(ObjectInput in) throws IOException {
		final Date from = new Date(in.readLong());
		final Date to = new Date(in.readLong());
		return new FromToPeriod(from, to);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(from.getTime());
		out.writeLong(to.getTime());
	}

}
