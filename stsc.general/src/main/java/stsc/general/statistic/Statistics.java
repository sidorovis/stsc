package stsc.general.statistic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.general.statistic.EquityCurve.Element;

public class Statistics {

	static Logger logger = LogManager.getLogger(Statistics.class.getSimpleName());

	private static final Class<?>[] emptyInvoker = {};
	private static final Object[] emptyValues = {};

	static class StatisticsInit {

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
		public double startMonthStDevGain = 0.0;
		public double startMonthMin = 0.0;
		public double startMonthMax = 0.0;

		public double month12AvGain = 0.0;
		public double month12StDevGain = 0.0;
		public double month12Min = 0.0;
		public double month12Max = 0.0;

		public double ddDurationAvGain = 0.0;
		public double ddDurationMax = 0.0;

		public double ddValueAvGain = 0.0;
		public double ddValueMax = 0.0;

		double getAvGain() {
			if (equityCurve.size() == 0) {
				logger.warn("strange equityCurve, seems that algorithms won't trade that time");
				return 0.0;
			}
			return equityCurve.getLastElement().value;
		}

		public String toString() {
			return "curve(" + equityCurve.toString() + ")";
		}

		void copyMoneyEquityCurve() {
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
	private double startMonthStDevGain = 0.0;
	private double startMonthMax = 0.0;
	private double startMonthMin = 0.0;

	private double month12AvGain = 0.0;
	private double month12StDevGain = 0.0;
	private double month12Min = 0.0;
	private double month12Max = 0.0;

	private double ddDurationAvGain = 0.0;
	private double ddDurationMax = 0.0;
	private double ddValueAvGain = 0.0;
	private double ddValueMax = 0.0;

	@NotPrint
	private EquityCurve equityCurveInMoney;

	static public StatisticsInit createInit() {
		return new StatisticsInit();
	}

	Statistics(StatisticsInit init) {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
		equityCurveInMoney = init.equityCurveInMoney;
	}

	public Statistics(final Map<String, Double> list) {
		for (Entry<String, Double> i : list.entrySet()) {
			switch (i.getKey()) {
			case "getAvGain":
				setAvGain(i.getValue());
				break;
			case "getPeriod":
				setPeriod(i.getValue());
				break;
			case "getFreq":
				setFreq(i.getValue());
				break;
			case "getWinProb":
				setWinProb(i.getValue());
				break;
			case "getAvWin":
				setAvWin(i.getValue());
				break;
			case "getAvLoss":
				setAvLoss(i.getValue());
				break;
			case "getAvWinAvLoss":
				setAvWinAvLoss(i.getValue());
				break;
			case "getMaxWin":
				setMaxWin(i.getValue());
				break;
			case "getMaxLoss":
				setMaxLoss(i.getValue());
				break;
			case "getKelly":
				setKelly(i.getValue());
				break;
			case "getSharpeRatio":
				setSharpeRatio(i.getValue());
				break;
			case "getStartMonthAvGain":
				setStartMonthAvGain(i.getValue());
				break;
			case "getStartMonthStDevGain":
				setStartMonthStDevGain(i.getValue());
				break;
			case "getStartMonthMax":
				setStartMonthMax(i.getValue());
				break;
			case "getStartMonthMin":
				setStartMonthMin(i.getValue());
				break;
			case "getMonth12AvGain":
				setMonth12AvGain(i.getValue());
				break;
			case "getMonth12StDevGain":
				setMonth12StDevGain(i.getValue());
				break;
			case "getMonth12Min":
				setMonth12Min(i.getValue());
				break;
			case "getMonth12Max":
				setMonth12Max(i.getValue());
				break;
			case "getDdDurationAvGain":
				setDdDurationAvGain(i.getValue());
				break;
			case "getDdDurationMax":
				setDdDurationMax(i.getValue());
				break;
			case "getDdValueAvGain":
				setDdValueAvGain(i.getValue());
				break;
			case "getDdValueMax":
				setDdValueMax(i.getValue());
				break;
			default:
				break;
			}
		}
	}

	private void calculateProbabilityStatistics(StatisticsInit init) {
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
		startMonthStDevGain = init.startMonthStDevGain;
		startMonthMax = init.startMonthMax;
		startMonthMin = init.startMonthMin;

		month12AvGain = init.month12AvGain;
		month12StDevGain = init.month12StDevGain;
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
	public double getStartMonthStDevGain() {
		return startMonthStDevGain;
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
	public double getMonth12StDevGain() {
		return month12StDevGain;
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

	public EquityCurve getEquityCurveInMoney() {
		return equityCurveInMoney;
	}

	//

	private void setAvGain(Double value) {
		avGain = value;
	}

	private void setPeriod(Double value) {
		period = value.intValue();
	}

	private void setWinProb(Double value) {
		winProb = value;
	}

	private void setFreq(Double value) {
		freq = value;
	}

	private void setAvWin(Double value) {
		avWin = value;
	}

	private void setAvLoss(Double value) {
		avLoss = value;
	}

	private void setAvWinAvLoss(Double value) {
		avWinAvLoss = value;
	}

	private void setKelly(Double value) {
		kelly = value;
	}

	private void setSharpeRatio(Double value) {
		sharpeRatio = value;
	}

	private void setMaxWin(Double value) {
		maxWin = value;
	}

	private void setMaxLoss(Double value) {
		maxLoss = value;
	}

	private void setStartMonthAvGain(Double value) {
		startMonthAvGain = value;
	}

	private void setStartMonthStDevGain(Double value) {
		startMonthStDevGain = value;
	}

	private void setStartMonthMax(Double value) {
		startMonthMax = value;
	}

	private void setStartMonthMin(Double value) {
		startMonthMin = value;
	}

	private void setMonth12AvGain(Double value) {
		month12AvGain = value;
	}

	private void setMonth12StDevGain(Double value) {
		month12StDevGain = value;
	}

	private void setMonth12Min(Double value) {
		month12Min = value;
	}

	private void setMonth12Max(Double value) {
		month12Max = value;
	}

	private void setDdDurationAvGain(Double value) {
		ddDurationAvGain = value;
	}

	private void setDdDurationMax(Double value) {
		ddDurationMax = value;
	}

	private void setDdValueAvGain(Double value) {
		ddValueAvGain = value;
	}

	private void setDdValueMax(Double value) {
		ddValueMax = value;
	}

	//

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

	public static Double invokeMethod(Statistics s, Method method) {
		if (method.isAnnotationPresent(PublicMethod.class)) {
			try {
				return (Double) method.invoke(s, emptyValues);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		}
		return 0.0;
	}

	public static Double invokeMethod(Statistics s, String methodName) {
		try {
			final Method method = Statistics.class.getMethod(methodName, emptyInvoker);
			return invokeMethod(s, method);
		} catch (NoSuchMethodException | IllegalArgumentException e) {

		}
		return 0.0;
	}

	@Override
	public String toString() {
		String result = "Statistics: \n";
		for (String methodName : Statistics.getStatisticsMethods()) {
			result += " " + methodName + " " + invokeMethod(this, methodName) + "\n";
		}
		return result;
	}
}