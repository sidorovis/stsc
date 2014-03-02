package stsc.statistic;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

public class Statistics {

	static public class StatisticsInit {

		public EquityCurve equityCurve = new EquityCurve();

		public int count = 0;

		public int winCount = 0;
		public int lossCount = 0;

		public double winSum = 0.0;
		public double lossSum = 0.0;

		public double getAvGain() throws StatisticsCalculationException {
			if (equityCurve.size() == 0)
				throw new StatisticsCalculationException("no elements at equity curve");
			return equityCurve.getLastElement().value;
		}

		public double maxWin = 0.0;
		public double maxLoss = 0.0;

	};

	static private double division(double a, double b) {
		if (b == 0.0)
			return 0.0;
		else
			return a / b;
	}

	private double avGain;
	private int period;
	private double freq;
	private double winProb;

	private double avWin;
	private double avLoss;
	private double avWinAvLoss;

	private double maxWin;
	private double maxLoss;

	private double kelly;

	private double sharpeRatio;

	static public StatisticsInit getInit() {
		return new StatisticsInit();
	}

	public Statistics(StatisticsInit init) throws StatisticsCalculationException {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
	}

	private void calculateProbabilityStatistics(StatisticsInit init) throws StatisticsCalculationException {
		avGain = init.getAvGain();
		period = init.equityCurve.size();

		freq = division(init.count, period);
		winProb = division(init.winCount, init.count);

		avWin = division(init.winSum, init.winCount);
		maxWin = init.maxWin;
		avLoss = Math.abs(division(init.lossSum, init.lossCount));
		maxLoss = -init.maxLoss;
		avWinAvLoss = division(avWin, avLoss);

		if (avWinAvLoss == 0.0)
			kelly = 0.0;
		else
			kelly = winProb - (1 - winProb) / avWinAvLoss;
	}

	private void calculateEquityStatistics(StatisticsInit init) {
		final int DAYS_PER_YEAR = 250;
		if (period > DAYS_PER_YEAR) {
			calculateMonthsStatistics(init);
			calculate12MonthsStatistics(init);
		}
	}

	private void calculate12MonthsStatistics(StatisticsInit init) {
//		LocalDate indexDate = new LocalDate(init.equityCurve.get(0).date);
//		LocalDate nextMonthBegin = indexDate.plusMonths(1).withDayOfMonth(1);
//
//		int index = init.equityCurve.find(nextMonthBegin.toDate());
//		LocalDate nextMonthBeginRealDate = new LocalDate(init.equityCurve.get(index).date);
	}

	private void calculateMonthsStatistics(StatisticsInit init) {

		int index = 0;

		LocalDate indexDate = new LocalDate(init.equityCurve.get(index).date);
		LocalDate monthAgo = indexDate.plusMonths(1);

		double indexValue = init.equityCurve.get(index).value;

		double monthsCapitalsSum = 0.0;
		ArrayList<Double> monthsDifferents = new ArrayList<>();

		final LocalDate lastDate = new LocalDate(init.equityCurve.getLastElement().date);
		
		while (monthAgo.isBefore(lastDate)) {
			index = init.equityCurve.find(monthAgo.toDate());

			double lastValue = init.equityCurve.get(index).value;
			double differentForMonth = lastValue - indexValue;
			
			monthsDifferents.add(differentForMonth);
			monthsCapitalsSum += differentForMonth;

			indexValue = lastValue;
			monthAgo = monthAgo.plusMonths(1);
		}

		final int REASONABLE_AMOUNT_OF_DAYS = 12;
		if (init.equityCurve.size() - index > REASONABLE_AMOUNT_OF_DAYS) {
			double lastValue = init.equityCurve.getLastElement().value;
			double differentForMonth = lastValue - indexValue;

			monthsDifferents.add(differentForMonth);
			monthsCapitalsSum += differentForMonth;
		}

		final double MONTH_PER_YEAR = 12.0;
		final double RISK_PERCENTS = 5.0;
		
		double sharpeAnnualReturn = (MONTH_PER_YEAR / monthsDifferents.size()) * monthsCapitalsSum;
		double sharpeStdDev = Math.sqrt(MONTH_PER_YEAR) * calculateStdDev(monthsCapitalsSum, monthsDifferents);

		sharpeRatio = (sharpeAnnualReturn - RISK_PERCENTS) / sharpeStdDev;
	}

	public double calculateStdDev(List<Double> elements) {
		double summ = 0.0;
		for (Double i : elements) {
			summ += i;
		}
		return calculateStdDev(summ, elements);
	}

	private double calculateStdDev(double summ, List<Double> elements) {
		double result = 0.0;
		int size = elements.size();
		double average = summ / size;
		for (Double i : elements) {
			result += Math.pow((average - i), 2);
		}
		result = Math.sqrt(result / size);
		return result;
	}

	public double getAvGain() {
		return avGain;
	}

	public int getPeriod() {
		return period;
	}

	public double getWinProb() {
		return winProb;
	}

	public double getFreq() {
		return freq;
	}

	public double getAvWin() {
		return avWin;
	}

	public double getAvLoss() {
		return avLoss;
	}

	public double getAvWinAvLoss() {
		return avWinAvLoss;
	}

	public double getKelly() {
		return kelly;
	}

	public double getSharpeRatio() {
		return sharpeRatio;
	}

	public double getMaxWin() {
		return maxWin;
	}

	public double getMaxLoss() {
		return maxLoss;
	}

}