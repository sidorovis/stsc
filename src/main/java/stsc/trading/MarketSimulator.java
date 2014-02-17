package stsc.trading;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
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
	private ArrayList<Algorithm> tradeAlgorithms = new ArrayList<Algorithm>();

	private Date from;
	private Date to;

	private List<String> processingStockList = new ArrayList<String>();

	private HashMap<String, StockIterator> stocks = new HashMap<String, StockIterator>();

	public MarketSimulator(MarketSimulatorSettings settings) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IOException, InterruptedException {
		this.stockStorage = settings.getStockStorage();
		broker = settings.getBroker();

		loadAlgorithms(settings);
		parseSimulationSettings(settings);
	}

	private void loadAlgorithms(MarketSimulatorSettings settings) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		for (String algorithmType : settings.getAlgorithmList()) {
			Class<?> classType = Class.forName(algorithmType);// "stsc.algorithms.SimpleTraderExample");
			Constructor<?> constructor = classType.getConstructor();
			Algorithm algo = (Algorithm) constructor.newInstance();
			algo.setBroker(broker);
			tradeAlgorithms.add(algo);
		}
	}

	private void parseSimulationSettings(MarketSimulatorSettings settings) {
		from = settings.getFrom();
		to = settings.getTo();

		processingStockList.addAll(settings.getStockList());
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
				if (stockDay != null) {
					if (stockDay.compareTo(currentDay) == 0)
						datafeed.put(stockName, stockDay);
					else {
						throw new Exception("Bad day returned for stock " + stockName + " for day "
								+ dateIterator.toDate());
					}
				}
			}
			for (Algorithm algorithm : tradeAlgorithms) {
				algorithm.process(currentDay.date, datafeed);
			}
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

	public ArrayList<Algorithm> getTradeAlgorithms() {
		return tradeAlgorithms;
	}
}
