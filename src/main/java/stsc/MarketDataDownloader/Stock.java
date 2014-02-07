package stsc.MarketDataDownloader;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Stock implements Serializable {

	private static final long serialVersionUID = 4471626546221264954L;
	final String name;
	List<Day> days = new ArrayList<Day>();

	static Stock newStockFromString(String n, String content)
			throws ParseException {
		Stock stock = new Stock(n);
		String[] lines = content.split("\n");

		Collections.reverse(Arrays.asList(lines));

		for (int i = 0; i < lines.length - 1; ++i)
			if (!lines[i].isEmpty())
				storeDataLine(stock, lines[i]);

		return stock;
	}

	Stock(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	void addDay(Day d) {
		days.add(d);
	}

	public boolean addDaysFromString(String newData) throws ParseException {
		String[] lines = newData.split("\n");

		Collections.reverse(Arrays.asList(lines));

		for (int i = 0; i < lines.length - 1; ++i)
			if (!lines[i].isEmpty())
				storeDataLine(this, lines[i]);
		
		return lines.length > 1;
	}

	Collection<Day> getDays() {
		return Collections.unmodifiableCollection(days);
	}

	String generatePartiallyDownloadLine() {
		Date lastDate = days.get(days.size() - 1).date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastDate);
		cal.add(Calendar.DATE, 1);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		return "http://ichart.yahoo.com/table.csv?s=" + name + "&a=" + month
				+ "&b=" + day + "&c=" + year;
	}

	static private void storeDataLine(Stock stock, String line)
			throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(line.substring(0,
				10));
		String[] tokens = line.split(",");
		double volume = Double.parseDouble(tokens[5]);
		double adj_close = Double.parseDouble(tokens[6]);

		Day newDay = new Day(date, Prices.fromTokens(tokens), volume, adj_close);
		stock.addDay(newDay);
	}
}
