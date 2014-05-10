package stsc.statistic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import stsc.statistic.EquityCurve.Element;

public class Statistics {

	static public class StatisticsInit {

		public EquityCurve equityCurve = new EquityCurve();
		public EquityCurve equityCurveInMoney;

		public int period = 0;

		public int count = 0;

		public int winCount = 0;
		public int lossCount = 0;

		public double winSum = 0.0;
		public double lossSum = 0.0;

		public double maxWin = 0.0;
		public double maxLoss = 0.0;

		public double sharpeRatio = 0.0;

		public double startMonthAvGain = 0.0;
		public double startMonthStdDevGain = 0.0;
		public double startMonthMin = 0.0;
		public double startMonthMax = 0.0;

		public double month12AvGain = 0.0;
		public double month12StdDevGain = 0.0;
		public double month12Min = 0.0;
		public double month12Max = 0.0;

		public double ddDurationAvGain = 0.0;
		public double ddDurationMax = 0.0;

		public double ddValueAvGain = 0.0;
		public double ddValueMax = 0.0;

		public double getAvGain() throws StatisticsCalculationException {
			if (equityCurve.size() == 0)
				throw new StatisticsCalculationException("no elements at equity curve");
			return equityCurve.getLastElement().value;
		}

		public String toString() {
			return "curve(" + equityCurve.toString() + ")";
		}

		public void copyMoneyEquityCurve() {
			equityCurveInMoney = equityCurve.clone();
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

	private double maxWin;
	private double maxLoss;

	private double kelly;

	private double sharpeRatio = 0.0;

	private double startMonthAvGain = 0.0;
	private double startMonthStdDevGain = 0.0;
	private double startMonthMax = 0.0;
	private double startMonthMin = 0.0;

	private double month12AvGain = 0.0;
	private double month12StdDevGain = 0.0;
	private double month12Min = 0.0;
	private double month12Max = 0.0;

	private double ddDurationAvGain = 0.0;
	private double ddDurationMax = 0.0;
	private double ddValueAvGain = 0.0;
	private double ddValueMax = 0.0;

	@NotPrint
	private EquityCurve equityCurveInMoney;

	static public StatisticsInit getInit() {
		return new StatisticsInit();
	}

	public Statistics(StatisticsInit init) throws StatisticsCalculationException {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
		equityCurveInMoney = init.equityCurveInMoney;
	}

	private void calculateProbabilityStatistics(StatisticsInit init) throws StatisticsCalculationException {
		avGain = init.getAvGain();
		period = init.period;

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
		sharpeRatio = init.sharpeRatio;

		startMonthAvGain = init.startMonthAvGain;
		startMonthStdDevGain = init.startMonthStdDevGain;
		startMonthMax = init.startMonthMax;
		startMonthMin = init.startMonthMin;

		month12AvGain = init.month12AvGain;
		month12StdDevGain = init.month12StdDevGain;
		month12Max = init.month12Max;
		month12Min = init.month12Min;

		ddDurationAvGain = init.ddDurationAvGain;
		ddDurationMax = init.ddDurationMax;
		ddValueAvGain = init.ddValueAvGain;
		ddValueMax = init.ddValueMax;
	}

	@PublicMethod
	public double getAvGain() {
		return avGain;
	}

	@PublicMethod
	public double getPeriod() {
		return period;
	}

	@PublicMethod
	public double getWinProb() {
		return winProb;
	}

	@PublicMethod
	public double getFreq() {
		return freq;
	}

	@PublicMethod
	public double getAvWin() {
		return avWin;
	}

	@PublicMethod
	public double getAvLoss() {
		return avLoss;
	}

	@PublicMethod
	public double getAvWinAvLoss() {
		return avWinAvLoss;
	}

	@PublicMethod
	public double getKelly() {
		return kelly;
	}

	@PublicMethod
	public double getSharpeRatio() {
		return sharpeRatio;
	}

	@PublicMethod
	public double getMaxWin() {
		return maxWin;
	}

	@PublicMethod
	public double getMaxLoss() {
		return maxLoss;
	}

	@PublicMethod
	public double getStartMonthAvGain() {
		return startMonthAvGain;
	}

	@PublicMethod
	public double getStartMonthStdDevGain() {
		return startMonthStdDevGain;
	}

	@PublicMethod
	public double getStartMonthMax() {
		return startMonthMax;
	}

	@PublicMethod
	public double getStartMonthMin() {
		return startMonthMin;
	}

	@PublicMethod
	public double getMonth12AvGain() {
		return month12AvGain;
	}

	@PublicMethod
	public double getMonth12StdDevGain() {
		return month12StdDevGain;
	}

	@PublicMethod
	public double getMonth12Min() {
		return month12Min;
	}

	@PublicMethod
	public double getMonth12Max() {
		return month12Max;
	}

	@PublicMethod
	public double getDdDurationAvGain() {
		return ddDurationAvGain;
	}

	@PublicMethod
	public double getDdDurationMax() {
		return ddDurationMax;
	}

	@PublicMethod
	public double getDdValueAvGain() {
		return ddValueAvGain;
	}

	@PublicMethod
	public double getDdValueMax() {
		return ddValueMax;
	}

	public void print(final String outputFile) throws IOException, IllegalArgumentException, IllegalAccessException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			print(writer);
		}
	}

	private void print(BufferedWriter outfile) throws IOException, IllegalArgumentException, IllegalAccessException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		final DecimalFormat decimalFormat = new DecimalFormat("#0.000");

		final Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {

			if (field.getAnnotation(NotPrint.class) != null)
				continue;

			if (Modifier.isStatic(field.getModifiers()))
				continue;

			outfile.append(field.getName()).append('\t');
			if (field.getType() == double.class)
				outfile.append(decimalFormat.format(field.get(this))).append('\n');
			else
				outfile.append(field.get(this).toString()).append('\n');
		}
		outfile.append('\n');

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final Element e = equityCurveInMoney.get(i);
			outfile.append(dateFormat.format(e.date)).append('\t').append(decimalFormat.format(e.value)).append('\n');
		}
	}

	@NotPrint
	private static Set<String> statisticsMethods = null;

	public synchronized static Set<String> getStatisticsMethods() {
		if (statisticsMethods == null) {
			statisticsMethods = new HashSet<>();
			final Method[] methods = Statistics.class.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(PublicMethod.class)) {
					statisticsMethods.add(method.getName());
				}
			}
		}
		return statisticsMethods;
	}
}