package stsc.trading;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import stsc.algorithms.Algorithm;
import stsc.common.Day;
import stsc.common.StockInterface;
import stsc.storage.StockStorage;

public class MarketSimulator {

	private StockStorage stockStorage;
	private Broker broker;
	private Algorithm tradeAlgorithm;

	private Date from;
	private Date to;

	private List<String> processingStockList = new ArrayList<String>();

	private class StockIterator {
		@SuppressWarnings("unused")
		StockInterface stock;
		ArrayList<Day> days;
		int currentIterator;

		public StockIterator(StockInterface stock, Date from) {
			this.stock = stock;
			days = stock.getDays();
			if (days.size() > 0 && days.get(0).date.compareTo(from) >= 0)
				currentIterator = 0;
			else {
				currentIterator = Collections.binarySearch(days, new Day(from));
				if (currentIterator < 0) {
					if (-currentIterator >= days.size())
						currentIterator = days.size();
					else
						currentIterator = -currentIterator - 1;
				}
			}
		}

		public boolean dataFound() {
			return currentIterator < days.size();
		}

		public Day getCurrentDayAndIncrement(Day currentDay) {
			// TODO: rebuild method mechanism for elegant search of the
			// necessary information
			if (currentIterator < days.size()) {
				Day day = days.get(currentIterator);
				int dayCompare = day.compareTo(currentDay);
				if (dayCompare == 0) {
					currentIterator++;
					return day;
				} else if (dayCompare < 0) {
					currentIterator = Collections.binarySearch(days, currentDay);
					if (currentIterator < 0) {
						currentIterator = -currentIterator;
						return null;
					}
					if (currentIterator >= 0 && currentIterator < days.size())
						return days.get(currentIterator);

				} else {
					return null;
				}
				return null;
			}
			return null;
		}
	}

	private HashMap<String, StockIterator> stocks = new HashMap<String, StockIterator>();

	public MarketSimulator(StockStorage stockStorage) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IOException, InterruptedException {
		this.stockStorage = stockStorage; //
		broker = new Broker();

		Class<?> classType = Class.forName("stsc.algorithms.SimpleTraderExample");
		Constructor<?> constructor = classType.getConstructor();
		tradeAlgorithm = (Algorithm) constructor.newInstance();

		tradeAlgorithm.setBroker(broker);

		parseSimulationSettings();
	}

	private void parseSimulationSettings() throws ParseException {
		DateFormat dateReader = new SimpleDateFormat("dd-MM-yyyy");

		from = dateReader.parse("30-10-2013");
		to = dateReader.parse("06-11-2013");

		processingStockList.add("aapl");
		processingStockList.add("gfi");
		processingStockList.add("no30");
		processingStockList.add("unexisted_stock");
		processingStockList.add("oldstock");
	}

	public void simulate() throws Exception {
		LocalDate dateIterator = new LocalDate(from);
		LocalDate endDate = new LocalDate(to);

		collectStocksFromStorage();

		while (dateIterator.isBefore(endDate)) {
			HashMap<String, Day> datafeed = new HashMap<String, Day>();

			Day currentDay = new Day(dateIterator.toDate());

			for (Map.Entry<String, StockIterator> i : stocks.entrySet()) {
				String stockName = i.getKey();
				StockIterator stockIterator = i.getValue();
				Day stockDay = stockIterator.getCurrentDayAndIncrement(currentDay);
				if (stockDay == null) {
					// TODO: think about deleting stocks with no data for period
				} else {
					if (stockDay.compareTo(currentDay) == 0)
						datafeed.put(stockName, stockDay);
					else {
						throw new Exception("Bad day returned for stock " + stockName + " for day "
								+ dateIterator.toDate());
					}
				}
			}

			tradeAlgorithm.process(currentDay.date, datafeed);
			dateIterator = dateIterator.plusDays(1);
		}
	}

	private void collectStocksFromStorage() {
		for (String i : processingStockList) {
			StockInterface stock = stockStorage.getStock(i);
			if (stock != null) {
				StockIterator stockIterator = new StockIterator(stock, from);
				if (stockIterator.dataFound()) {
					stocks.put(i, stockIterator);
				}
			}
		}
	}
}
