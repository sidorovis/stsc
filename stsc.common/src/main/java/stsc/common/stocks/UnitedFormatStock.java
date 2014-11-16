package stsc.common.stocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Queue;
import java.util.TimeZone;

import org.joda.time.LocalDate;

import stsc.common.Day;

public final class UnitedFormatStock extends Stock {

	public final static String EXTENSION = ".uf";

	private static final TimeZone timeZone;
	private static final DateFormat dateFormat;

	static {
		timeZone = TimeZone.getTimeZone("UTC");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(timeZone);
	}

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
		try (DataInputStream is = new DataInputStream(new FileInputStream(filePath))) {
			return readFromUniteFormatFile(is);
		}
	}

	public static UnitedFormatStock readFromUniteFormatFile(DataInputStream is) throws IOException {
		UnitedFormatStock s = null;
		String name = is.readUTF();
		s = new UnitedFormatStock(name);
		int daysLength = is.readInt();
		for (int i = 0; i < daysLength; ++i) {
			Date dayTime = Day.nullableTime(new Date(is.readLong()));
			double open = is.readDouble();
			double high = is.readDouble();
			double low = is.readDouble();
			double close = is.readDouble();
			double volume = is.readDouble();
			double adjClose = is.readDouble();
			Day newDay = new Day(dayTime, calculatePrices(open, high, low, close, adjClose), volume, adjClose);
			s.addDay(newDay);
		}
		return s;
	}

	private static Prices calculatePrices(double open, double high, double low, double close, double adjClose) {
		// final double diff_value = adjClose - close;
		return new Prices(open, high, low, close);
		// return new Prices(diff_value + open, diff_value + high, diff_value +
		// low, diff_value + close);
	}

	static private void storeDataLine(UnitedFormatStock stock, String line) throws ParseException {
		Date date;
		final String lineDate = line.substring(0, 10);
		try {
			date = Day.nullableTime(dateFormat.parse(lineDate));
			String[] tokens = line.split(",");
			double volume = Double.parseDouble(tokens[5]);
			double adj_close = Double.parseDouble(tokens[6]);
			Day newDay = new Day(date, Prices.fromTokens(tokens), volume, adj_close);
			stock.addDay(newDay);
		} catch (ParseException e) {
			throw new ParseException(e.toString() + " while parsing data: " + lineDate, 1);
		} catch (NumberFormatException e) {
			throw new ParseException(e.toString() + " while parsing data: '" + line + "' ", 1);
		}
	}

	public UnitedFormatStock(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void storeUniteFormatToFolder(String folderPath) throws IOException {
		storeUniteFormat(folderPath + "/" + getName() + EXTENSION);
	}

	public void storeUniteFormat(String filePath) throws IOException {
		try (DataOutputStream os = new DataOutputStream(new FileOutputStream(filePath))) {
			storeUniteFormat(os);
		}
	}

	public void storeUniteFormat(DataOutputStream os) throws IOException {
		os.writeUTF(name);
		os.writeInt(days.size());
		for (Day day : days) {
			os.writeLong(Day.nullableTime(day.date).getTime());
			os.writeDouble(day.prices.open);
			os.writeDouble(day.prices.high);
			os.writeDouble(day.prices.low);
			os.writeDouble(day.prices.close);
			os.writeDouble(day.volume);
			os.writeDouble(day.adjClose);
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
		if (new LocalDate(lastDate).equals(new LocalDate(new Date()))) {
			return "";
		}
		if (new LocalDate(lastDate).plusDays(1).equals(new LocalDate(new Date()))) {
			return "";
		}
		if (new LocalDate(lastDate).plusDays(2).equals(new LocalDate(new Date()))) {
			return "";
		}
		cal.add(Calendar.DATE, 1);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		return "http://ichart.yahoo.com/table.csv?s=" + name + "&a=" + month + "&b=" + day + "&c=" + year;
	}

	public static void loadStockList(String folderData, Queue<String> fileNames) {
		File folder = new File(folderData);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles, new FileComparator());
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(EXTENSION)) {
				fileNames.add(filename.substring(0, filename.length() - EXTENSION.length()));
			}
		}
	}

	public static String generatePath(String dataFolder, String stockName) {
		return dataFolder + stockName + EXTENSION;
	}

	private final static class FileComparator implements Comparator<File> {

		@Override
		public int compare(File left, File right) {
			return getStockName(left).compareTo(getStockName(right));
		}

		private String getStockName(File file) {
			String filename = file.getName();
			filename = filename.substring(0, filename.length() - EXTENSION.length());
			return filename;
		}

	}

}
