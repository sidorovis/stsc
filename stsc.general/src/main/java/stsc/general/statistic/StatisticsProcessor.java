package stsc.general.statistic;

import java.util.List;

import stsc.common.Day;
import stsc.general.trading.TradingLog;

public final class StatisticsProcessor {

	private final double commision;

	private EquityProcessor equityProcessor;

	public StatisticsProcessor(final TradingLog tradingLog, double commision) {
		this.commision = commision;
		this.equityProcessor = new EquityProcessor(this, tradingLog);
	}

	public StatisticsProcessor(final TradingLog tradingLog) {
		this(tradingLog, 0.001);
	}

	public void setStockDay(String stockName, Day stockDay) {
		equityProcessor.setStockDay(stockName, stockDay);
	}

	public double processEod(boolean debug) {
		return equityProcessor.processEod(debug);
	}

	public double processEod() {
		return processEod(false);
	}

	public Statistics calculate() {
		Statistics statisticsData = equityProcessor.calculate();
		equityProcessor = null;
		return statisticsData;
	}

	public double getCommision() {
		return commision;
	}

	public static double calculateStdDev(double summ, List<Double> elements) {
		double result = 0.0;
		int size = elements.size();
		double average = summ / size;
		for (Double i : elements) {
			result += Math.pow((average - i), 2);
		}
		result = Math.sqrt(result / size);
		return result;
	}

}
