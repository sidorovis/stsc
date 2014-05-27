package stsc.trading;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.StockStorage;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsProcessor;
import stsc.statistic.StatisticsCalculationException;
import stsc.storage.ExecutionStarter;

public class TradeProcessor {

	private final Broker broker;
	private final ExecutionStarter executionsStarter;

	private DayIteratorStorage stocks;

	public TradeProcessor(final TradeProcessorInit settings) throws BadAlgorithmException {
		this.broker = settings.getBroker();
		this.stocks = new DayIteratorStorage(settings.getPeriod().getFrom());
		this.executionsStarter = settings.getExecutionsStorage().initialize(broker);
	}

	public Statistics simulate(final FromToPeriod period) throws StatisticsCalculationException, BadSignalException {
		final StatisticsProcessor statisticsProcessor = new StatisticsProcessor(broker.getTradingLog());

		LocalDate dayIterator = new LocalDate(period.getFrom());
		LocalDate endDate = new LocalDate(period.getTo());

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

						executionsStarter.runStockAlgorithms(stockName, stockDay);

						datafeed.put(stockName, stockDay);
					} else {
						throw new StatisticsCalculationException("Bad day returned for stock " + stockName + " for day " + today);
						// TODO only for debugging, delete it later
					}
				}
			}
			if (!datafeed.isEmpty()) {
				executionsStarter.runEodAlgorithms(today, datafeed);
				statisticsProcessor.processEod();
			}
			dayIterator = dayIterator.plusDays(1);
		}
		return statisticsProcessor.calculate();
	}

	private void collectStocksFromStorage() {
		final StockStorage stockStorage = broker.getStockStorage();
		for (String i : stockStorage.getStockNames()) {
			stocks.add(stockStorage.getStock(i));
		}
	}

	public ExecutionStarter getExecutionStorage() {
		return executionsStarter;
	}
}
