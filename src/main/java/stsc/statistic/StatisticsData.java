package stsc.statistic;

import java.util.ArrayList;

public class StatisticsData {

	static public class StatisticsDataInit {
		public ArrayList<Double> equityCurve = new ArrayList<>();

		public int count = 0;

		public int winCount = 0;
		public int lossCount = 0;

		public double winSum = 0.0;
		public double lossSum = 0.0;

		public double getAvGain() throws StatisticsCalculationException {
			equityCurve.size();
			if (equityCurve.size() == 0)
				throw new StatisticsCalculationException("no elements at equity curve");
			return equityCurve.get(equityCurve.size() - 1);
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
	
	static public StatisticsDataInit getInit() {
		return new StatisticsDataInit();
	}

	public StatisticsData(StatisticsDataInit init) throws StatisticsCalculationException {
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
			kelly = winProb - ( 1 - winProb ) / avWinAvLoss;
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

}