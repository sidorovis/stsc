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

import stsc.algorithms.EodAlgorithmInterface;
import stsc.common.Day;
import stsc.common.StockInterface;
import stsc.statistic.EquityCurve;
import stsc.storage.SignalsStorage;
import stsc.storage.StockStorage;
import stsc.storage.StockStorageCache;

public class MarketSimulator {

	private StockStorage stockStorage;
	private Broker broker;
	private SignalsStorage signalsStorage = new SignalsStorage();

	// TODO private HashMap<String, StockAlgorithmInterface >
	private HashMap<String, EodAlgorithmInterface> tradeAlgorithms = new HashMap<String, EodAlgorithmInterface>();

	private Date from;
	private Date to;

	private List<String> processingStockList = new ArrayList<String>();

	private StockStorageCache stockStorageCache = new StockStorageCache();
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
		for (Execution executionDescription : settings.getExecutionsList()) {
			Class<?> classType = Class.forName(executionDescription.algorithmName);
			Constructor<?> constructor = classType.getConstructor();

			EodAlgorithmInterface algo = (EodAlgorithmInterface) constructor.newInstance();
			algo.setExecutionName(executionDescription.executionName);
			algo.setBroker(broker);
			algo.setSignalsStorage(signalsStorage);

			tradeAlgorithms.put(executionDescription.executionName, algo);
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

			Date today = dateIterator.toDate();
			Day currentDay = new Day(today);

			for (Map.Entry<String, StockIterator> i : stocks.entrySet()) {
				String stockName = i.getKey();
				StockIterator stockIterator = i.getValue();
				Day stockDay = stockIterator.getCurrentDayAndIncrement(currentDay);
				if (stockDay != null) {
					if (stockDay.compareTo(currentDay) == 0)
						datafeed.put(stockName, stockDay);
					else {
						throw new Exception("Bad day returned for stock " + stockName + " for day " + today);
					}
				}
			}
			broker.setToday(today);
			for (Map.Entry<String, EodAlgorithmInterface> i : tradeAlgorithms.entrySet()) {
				i.getValue().process(today, datafeed);
			}
			dateIterator = dateIterator.plusDays(1);
		}
	}

	public void calculateStatistics() {
		// EquityCurve equityCurve =
		new EquityCurve(broker.getTradingLog(), stockStorageCache);
		// TODO calculate statistics
	}

	private void collectStocksFromStorage() {
		for (String i : processingStockList) {
			StockInterface stock = stockStorage.getStock(i);
			addStock(i, stock);
		}
	}

	private void addStock(String name, StockInterface stock) {
		if (stock != null) {
			StockIterator stockIterator = new StockIterator(stock, from);
			if (stockIterator.dataFound()) {
				stocks.put(name, stockIterator);
				stockStorageCache.updateStock(stock);
			}
		}
	}

	public HashMap<String, EodAlgorithmInterface> getTradeAlgorithms() {
		return tradeAlgorithms;
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}
}
