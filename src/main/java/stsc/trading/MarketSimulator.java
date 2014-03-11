package stsc.trading;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.EodAlgorithmInterface;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.algorithms.StockAlgorithmInterface;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.statistic.StatisticsProcessor;
import stsc.statistic.StatisticsCalculationException;
import stsc.storage.BadSignalException;
import stsc.storage.DayIteratorStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.DayIterator;
import stsc.storage.StockStorage;

public class MarketSimulator {

	class StockExecutions {
		public HashMap<String, StockAlgorithmInterface> map = new HashMap<>();

	}

	class StockAlgorithms {
		public HashMap<String, StockExecutions> algorithms = new HashMap<>();

		public void addExecution(String executionName, String stockName, StockAlgorithmInterface algo) {
			StockExecutions se = algorithms.get(executionName);
			if (se == null) {
				se = algorithms.put(executionName, new StockExecutions());
			}
			se.map.put(stockName, algo);
		}

		public StockExecutions get(String executionName) {
			return algorithms.get(executionName);
		}
	}

	private StockStorage stockStorage;
	private Broker broker;
	private StatisticsProcessor statistics;
	private SignalsStorage signalsStorage = new SignalsStorage();

	StockAlgorithms stockAlgorithms = new StockAlgorithms();
	private HashMap<String, EodAlgorithmInterface> tradeAlgorithms = new HashMap<>();

	private Date from;
	private Date to;

	private List<String> processingStockList = new ArrayList<String>();

	private DayIteratorStorage stocks;

	public MarketSimulator(MarketSimulatorSettings settings) throws BadAlgorithmException {
		this.stockStorage = settings.getStockStorage();
		this.broker = settings.getBroker();
		this.statistics = new StatisticsProcessor(broker.getTradingLog());

		parseSimulationSettings(settings);
		loadAlgorithms(settings);

		this.stocks = new DayIteratorStorage(from);
	}

	private void loadAlgorithms(MarketSimulatorSettings settings) throws BadAlgorithmException {
		for (StockAlgorithmExecution execution : settings.getStockExecutionsList()) {

			for (String stockName : processingStockList) {
				StockAlgorithmInterface algo = execution.getInstance(signalsStorage);
				stockAlgorithms.addExecution(execution.getName(), stockName, algo);
			}

		}
		for (EodAlgorithmExecution execution : settings.getEodExecutionsList()) {
			EodAlgorithmInterface algo = execution.getInstance(broker, signalsStorage);
			tradeAlgorithms.put(execution.getName(), algo);
		}
	}

	private void parseSimulationSettings(MarketSimulatorSettings settings) {
		from = settings.getFrom();
		to = settings.getTo();
		processingStockList.addAll(settings.getStockList());
	}

	public void simulate() throws StatisticsCalculationException, BadSignalException {
		LocalDate dayIterator = new LocalDate(from);
		LocalDate endDate = new LocalDate(to);

		collectStocksFromStorage();

		while (dayIterator.isBefore(endDate)) {
			HashMap<String, Day> datafeed = new HashMap<String, Day>();

			Date today = dayIterator.toDate();
			Day currentDay = new Day(today);

			broker.setToday(today);

			for (Entry<String, DayIterator> stock : stocks) {
				DayIterator stockIterator = stock.getValue();
				Day stockDay = stockIterator.getCurrentDayAndNext(currentDay);
				if (stockDay != null) {
					String stockName = stock.getKey();

					if (stockDay.compareTo(currentDay) == 0) {
						statistics.setStockDay(stockName, stockDay);

						for(Map.Entry<String, StockExecutions> sPair : stockAlgorithms.algorithms.entrySet()){
							for (Map.Entry<String, StockAlgorithmInterface> algo : sPair.getValue().map.entrySet()) {
								algo.getValue().process(algo.getKey(), stockDay);
							}
						}

						datafeed.put(stockName, stockDay);
					} else {
						throw new StatisticsCalculationException("Bad day returned for stock " + stockName
								+ " for day " + today);
						// TODO only for debugging, delete it later
					}
				}
			}
			for (Map.Entry<String, EodAlgorithmInterface> i : tradeAlgorithms.entrySet()) {
				i.getValue().process(today, datafeed);
			}
			statistics.processEod();
			dayIterator = dayIterator.plusDays(1);
		}
		statistics.calculate();
	}

	private void collectStocksFromStorage() {
		for (String i : processingStockList) {
			Stock stock = stockStorage.getStock(i);
			stocks.add(stock);
		}
	}

	public HashMap<String, EodAlgorithmInterface> getTradeAlgorithms() {
		return tradeAlgorithms;
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}
}
