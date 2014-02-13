package stsc.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class Stock implements Serializable {

	private static final long serialVersionUID = 4471626546221264954L;
	final String name;
	ArrayList<Day> days = new ArrayList<Day>();

	public static Stock readFromBinFile(String filePath)
			throws ClassNotFoundException, IOException {
		Stock s = null;
		ObjectInputStream oi = null;
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				filePath));
		try {
			oi = new ObjectInputStream(is);
			s = (Stock) oi.readObject();
		} finally {
			oi.close();
		}
		return s;
	}

	public static Stock readFromCsvFile(String name, String filePath)
			throws IOException, ParseException {
		byte[] data = Files.readAllBytes(Paths.get(filePath));
		String content = new String(data);
		return Stock.newFromString(name, content);
	}

	public static Stock newFromString(String n, String content) throws ParseException {
		Stock stock = new Stock(n);
		String[] lines = content.split("\n");

		Collections.reverse(Arrays.asList(lines));

		for (int i = 0; i < lines.length - 1; ++i)
			if (!lines[i].isEmpty())
				storeDataLine(stock, lines[i]);

		return stock;
	}

	public Stock(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	void addDay(Day d) {
		days.add(d);
	}

	public void store(String filePath) throws FileNotFoundException, IOException {
		ObjectOutputStream outFile = null;
		outFile = new ObjectOutputStream(new FileOutputStream(filePath));
		outFile.writeObject(this);
		outFile.close();
	}

	public boolean addDaysFromString(String newData) throws ParseException {
		String[] lines = newData.split("\n");

		Collections.reverse(Arrays.asList(lines));

		for (int i = 0; i < lines.length - 1; ++i)
			if (!lines[i].isEmpty())
				storeDataLine(this, lines[i]);

		return lines.length > 1;
	}

	public Collection<Day> getDays() {
		return Collections.unmodifiableCollection(days);
	}

	public ArrayList<Day> getDaysAsArrayList() {
		return days;
	}

	public String generatePartiallyDownloadLine() {
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
