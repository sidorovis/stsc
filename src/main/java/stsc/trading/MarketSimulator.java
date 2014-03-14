package stsc.trading;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmExecution;
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

	class Executions {
		// execution name to stock algorithms
		public HashMap<String, StockAlgorithm> map = new HashMap<>();

		void simulate(String stockName, final Day newDay) throws BadSignalException {
			for (Map.Entry<String, StockAlgorithm> sPair : map.entrySet()) {
				sPair.getValue().process(stockName, newDay);
			}
		}
	}

	class StockAlgorithms {
		// stock to execution map
		public HashMap<String, Executions> stockToExecution = new HashMap<>();

		public void addExecutionOnStock(String stockName, String executionName, StockAlgorithm algo) {
			Executions se = stockToExecution.get(stockName);
			if (se == null) {
				se = stockToExecution.put(stockName, new Executions());
			}
			se.map.put(executionName, algo);
		}

		public void simulate(String stockName, final Day newDay) throws BadSignalException {
			Executions e = stockToExecution.get(stockName);
			if (e != null)
				e.simulate(stockName, newDay);
		}
	}

	private StockStorage stockStorage;
	private Broker broker;
	private StatisticsProcessor statistics;
	private SignalsStorage signalsStorage = new SignalsStorage();

	StockAlgorithms stockAlgorithms = new StockAlgorithms();
	private HashMap<String, EodAlgorithm> tradeAlgorithms = new HashMap<>();

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
				StockAlgorithm algo = execution.getInstance(signalsStorage,new AlgorithmSettings());
				stockAlgorithms.addExecutionOnStock(stockName, execution.getName(), algo);
			}
		}
		for (EodAlgorithmExecution execution : settings.getEodExecutionsList()) {
			EodAlgorithm algo = execution.getInstance(broker, signalsStorage, new AlgorithmSettings());
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

						stockAlgorithms.simulate(stockName, stockDay);

						datafeed.put(stockName, stockDay);
					} else {
						throw new StatisticsCalculationException("Bad day returned for stock " + stockName
								+ " for day " + today);
						// TODO only for debugging, delete it later
					}
				}
			}
			for (Map.Entry<String, EodAlgorithm> i : tradeAlgorithms.entrySet()) {
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

	public HashMap<String, EodAlgorithm> getTradeAlgorithms() {
		return tradeAlgorithms;
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}
}
