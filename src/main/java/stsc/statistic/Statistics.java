package stsc.statistic;

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
			return equityCurve.getLastValue();
		}

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
		avLoss = Math.abs(division(init.lossSum, init.lossCount));
		avWinAvLoss = division(avWin, avLoss);

		if (avWinAvLoss == 0.0)
			kelly = 0.0;
		else
			kelly = winProb - (1 - winProb) / avWinAvLoss;
	}

	private void calculateEquityStatistics(StatisticsInit init) {
		final int DAYS_PER_YEAR = 250;
		if (period > DAYS_PER_YEAR)
			calculateMonthsStatistics(init);
	}

	private void calculateMonthsStatistics(StatisticsInit init) {
		LocalDate initDate = new LocalDate(init.equityCurve.get(0).date);
		int lastMonthOfTheYear = initDate.getMonthOfYear();
		
		for (int i = 1; i < init.equityCurve.size(); ++i) {
			LocalDate current = new LocalDate(init.equityCurve.get(i).date);
			int currentMonthOfYear = current.getMonthOfYear();
			if (currentMonthOfYear != lastMonthOfTheYear) {
				
			}
		}
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

}