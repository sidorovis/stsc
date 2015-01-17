package stsc.general.trading;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsProcessor;
import stsc.storage.ExecutionStarter;

public class TradeProcessor {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	static Logger logger = LogManager.getLogger(TradeProcessor.class.getSimpleName());

	private final BrokerImpl broker;
	private final ExecutionStarter executionsStarter;

	private DayIteratorStorage stocks;

	public TradeProcessor(final TradeProcessorInit settings) throws BadAlgorithmException {
		this.broker = settings.getBrokerImpl();
		this.stocks = new DayIteratorStorage(settings.getPeriod().getFrom());
		this.executionsStarter = settings.getExecutionsStorage().initialize(broker);
	}

	public Statistics simulate(final FromToPeriod period, Set<String> stockNames) throws BadSignalException {
		collectStocksFromStorage(stockNames);
		return startSimulationProcess(period).calculate();
	}

	public Statistics simulate(final FromToPeriod period) throws BadSignalException {
		collectStocksFromStorage();
		return startSimulationProcess(period).calculate();
	}

	private StatisticsProcessor startSimulationProcess(final FromToPeriod period) throws BadSignalException {
		final StatisticsProcessor statisticsProcessor = new StatisticsProcessor(broker.getTradingLog());
		LocalDate dayIterator = new LocalDate(period.getFrom());
		final LocalDate endDate = new LocalDate(period.getTo());
		while (dayIterator.isBefore(endDate)) {
			final HashMap<String, Day> datafeed = new HashMap<String, Day>();
			final Date today = dayIterator.toDate();
			final Day currentDay = new Day(today);

			broker.setToday(today);
			for (Entry<String, DayIterator> stock : stocks) {
				final DayIterator stockIterator = stock.getValue();
				final Optional<Day> stockDayPtr = stockIterator.getCurrentDayAndNext(currentDay);
				if (stockDayPtr.isPresent()) {
					final Day stockDay = stockDayPtr.get();
					final String stockName = stock.getKey();
					if (stockDay.compareTo(currentDay) == 0) {
						statisticsProcessor.setStockDay(stockName, stockDay);
						executionsStarter.runStockAlgorithms(stockName, stockDay);
						datafeed.put(stockName, stockDay);
					} else {
						logger.error("Bad day returned for stock " + stockName + " for day " + today);
						// TODO delete me i'm for testing
						throw new RuntimeException("Test exception");
					}
				}
			}
			if (!datafeed.isEmpty()) {
				executionsStarter.runEodAlgorithms(today, datafeed);
				statisticsProcessor.processEod();
			}
			dayIterator = dayIterator.plusDays(1);
		}
		return statisticsProcessor;
	}

	private void collectStocksFromStorage(final Set<String> stockNames) {
		final StockStorage stockStorage = broker.getStockStorage();
		for (String i : stockStorage.getStockNames()) {
			if (stockNames.contains(i)) {
				addStock(stockStorage.getStock(i));
			}
		}
	}

	private void collectStocksFromStorage() {
		final StockStorage stockStorage = broker.getStockStorage();
		for (String i : stockStorage.getStockNames()) {
			addStock(stockStorage.getStock(i));
		}
	}

	private void addStock(Optional<Stock> stock) {
		if (stock.isPresent()) {
			stocks.add(stock.get());
		}
	}

	public ExecutionStarter getExecutionStorage() {
		return executionsStarter;
	}
}
