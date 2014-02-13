package stsc.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Stock implements Serializable {

	private static final long serialVersionUID = 4471626546221264954L;
	final String name;
	ArrayList<Day> days = new ArrayList<Day>();

	public static Stock readFromCsvFile(String name, String filePath) throws IOException, ParseException {
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

	public static Stock readFromUniteFormatFile(String filePath) throws IOException {
		Stock s = null;
		DataInputStream is = new DataInputStream(new FileInputStream(filePath));
		String name = is.readUTF();
		s = new Stock(name);
		int daysLength = is.readInt();
		for (int i = 0; i < daysLength; ++i) {
			Date dayTime = new Date(is.readLong());
			double open = is.readDouble();
			double high = is.readDouble();
			double low = is.readDouble();
			double close = is.readDouble();
			double volume = is.readDouble();
			double adjClose = is.readDouble();
			Day newDay = new Day(dayTime, new Prices(open, high, low, close), volume, adjClose);
			s.addDay(newDay);
		}
		is.close();
		return s;
	}

	static private void storeDataLine(Stock stock, String line) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(line.substring(0, 10));
		String[] tokens = line.split(",");
		double volume = Double.parseDouble(tokens[5]);
		double adj_close = Double.parseDouble(tokens[6]);

		Day newDay = new Day(date, Prices.fromTokens(tokens), volume, adj_close);
		stock.addDay(newDay);
	}

	public Stock(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void storeUniteFormat(String filePath) throws IOException {
		DataOutputStream os = new DataOutputStream(new FileOutputStream(filePath));
		os.writeUTF(name);
		os.writeInt(days.size());
		for (Day day : days) {
			os.writeLong(day.date.getTime());
			os.writeDouble(day.prices.open);
			os.writeDouble(day.prices.high);
			os.writeDouble(day.prices.low);
			os.writeDouble(day.prices.close);
			os.writeDouble(day.volume);
			os.writeDouble(day.adj_close);
		}
		os.close();
	}

	private void addDay(Day d) {
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

	public ArrayList<Day> getDays() {
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
		return "http://ichart.yahoo.com/table.csv?s=" + name + "&a=" + month + "&b=" + day + "&c=" + year;
	}

}
