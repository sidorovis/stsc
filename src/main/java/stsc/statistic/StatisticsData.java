package stsc.statistic;

import java.util.ArrayList;

public class StatisticsData {
	private final ArrayList<Double> equityCurve;
	private double roi;
	
	public StatisticsData(ArrayList<Double> equityCurve) {
		this.equityCurve = equityCurve;
		roi = equityCurve.get(equityCurve.size() - 1);
	}

	public ArrayList<Double> getEquityCurve() {
		return equityCurve;
	}

	public double getRoi() {
		return roi;
	}
}