package stsc.trading;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.StockExecution;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.signals.BadSignalException;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsProcessor;
import stsc.statistic.StatisticsCalculationException;
import stsc.storage.DayIteratorStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.DayIterator;
import stsc.storage.StockStorage;

public class MarketSimulator {

	private StockStorage stockStorage;
	private Broker broker;
	private StatisticsProcessor statisticsProcessor;
	private Statistics statistics;
	private SignalsStorage signalsStorage = new SignalsStorage();
	private ExecutionsStorage executionsStorage;

	private Date from;
	private Date to;

	private List<String> processingStockList = new ArrayList<String>();

	private DayIteratorStorage stocks;

	public MarketSimulator(MarketSimulatorSettings settings) throws BadAlgorithmException {
		this.stockStorage = settings.getStockStorage();
		this.broker = settings.getBroker();
		this.statisticsProcessor = new StatisticsProcessor(broker.getTradingLog());
		parseSimulationSettings(settings);
		this.stocks = new DayIteratorStorage(from);
		loadAlgorithms(settings);
	}

	public MarketSimulator(MarketSimulatorSettings settings, ExecutionsStorage executionsStorage)
			throws BadAlgorithmException {
		this.stockStorage = settings.getStockStorage();
		this.broker = settings.getBroker();
		this.statisticsProcessor = new StatisticsProcessor(broker.getTradingLog());
		parseSimulationSettings(settings);
		this.stocks = new DayIteratorStorage(from);
		this.executionsStorage = executionsStorage;
		executionsStorage.initializeExecutions(signalsStorage, broker);

	}

	private void loadAlgorithms(MarketSimulatorSettings settings) throws BadAlgorithmException {
		executionsStorage = new ExecutionsStorage(processingStockList);
		for (StockExecution i : settings.getStockExecutionsList()) {
			executionsStorage.addStockExecution(i);
		}
		for (EodExecution i : settings.getEodExecutionsList()) {
			executionsStorage.addEodExecution(i);
		}
		executionsStorage.initializeExecutions(signalsStorage, broker);
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
						statisticsProcessor.setStockDay(stockName, stockDay);

						executionsStorage.runStockAlgorithms(stockName, stockDay);

						datafeed.put(stockName, stockDay);
					} else {
						throw new StatisticsCalculationException("Bad day returned for stock " + stockName
								+ " for day " + today);
						// TODO only for debugging, delete it later
					}
				}
			}
			if (!datafeed.isEmpty()) {
				executionsStorage.runEodAlgorithms(today, datafeed);
				statisticsProcessor.processEod();
			}
			dayIterator = dayIterator.plusDays(1);
		}
		statistics = statisticsProcessor.calculate();
	}

	private void collectStocksFromStorage() {
		for (String i : processingStockList) {
			Stock stock = stockStorage.getStock(i);
			stocks.add(stock);
		}
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}

	public ExecutionsStorage getExecutionStorage() {
		return executionsStorage;
	}

	public void printStatistics(final String outputFile) throws IOException, IllegalArgumentException,
			IllegalAccessException {
		if (statistics == null)
			return;
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			statistics.print(writer);
		}
	}

}
