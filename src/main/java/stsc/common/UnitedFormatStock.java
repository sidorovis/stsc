package stsc.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class UnitedFormatStock extends Stock {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	final String name;
	ArrayList<Day> days = new ArrayList<Day>();

	public static UnitedFormatStock readFromCsvFile(String name, String filePath) throws IOException, ParseException {
		byte[] data = Files.readAllBytes(Paths.get(filePath));
		String content = new String(data);
		return UnitedFormatStock.newFromString(name, content);
	}

	public static UnitedFormatStock newFromString(String n, String content) throws ParseException {
		UnitedFormatStock stock = new UnitedFormatStock(n);
		String[] lines = content.split("\n");
		Collections.reverse(Arrays.asList(lines));
		for (int i = 0; i < lines.length - 1; ++i)
			if (!lines[i].isEmpty())
				storeDataLine(stock, lines[i]);
		return stock;
	}

	public static UnitedFormatStock readFromUniteFormatFile(String filePath) throws IOException {
		UnitedFormatStock s = null;
		try (DataInputStream is = new DataInputStream(new FileInputStream(filePath))) {
			String name = is.readUTF();
			s = new UnitedFormatStock(name);
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
		}
		return s;
	}

	static private Date nullableTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date result = cal.getTime();
		return result;
	}

	static private void storeDataLine(UnitedFormatStock stock, String line) throws ParseException {
		Date date = nullableTime(dateFormat.parse(line.substring(0, 10)));
		String[] tokens = line.split(",");
		double volume = Double.parseDouble(tokens[5]);
		double adj_close = Double.parseDouble(tokens[6]);

		Day newDay = new Day(date, Prices.fromTokens(tokens), volume, adj_close);
		stock.addDay(newDay);
	}

	public UnitedFormatStock(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stsc.common.StockInterface#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void storeUniteFormat(String filePath) throws IOException {
		try (DataOutputStream os = new DataOutputStream(new FileOutputStream(filePath))) {
			os.writeUTF(name);
			os.writeInt(days.size());
			for (Day day : days) {
				os.writeLong(nullableTime(day.date).getTime());
				os.writeDouble(day.prices.open);
				os.writeDouble(day.prices.high);
				os.writeDouble(day.prices.low);
				os.writeDouble(day.prices.close);
				os.writeDouble(day.volume);
				os.writeDouble(day.adj_close);
			}
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see stsc.common.StockInterface#getDays()
	 */
	@Override
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
