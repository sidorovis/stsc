package stsc.algorithms.stock.patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.stock.geometry.LeastSquaresStraightStdDev;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalContainer;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public abstract class GeometryTriangleStockAlgorithmBase extends StockAlgorithm {

	private final String maxSubExecutionName;
	private final String maxLineName;
	private final LeastSquaresStraightStdDev maxLine;

	private final String minSubExecutionName;
	private final String minLineName;
	private final LeastSquaresStraightStdDev minLine;

	private final Double acceptableLineLevel;

	private final Double acceptableXfrom;
	private final Double acceptableXto;

	public GeometryTriangleStockAlgorithmBase(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() < 2) {
			throw new BadAlgorithmException(WedgePattern.class + " algorithm require at least two sub algorithms, for max and min lines.");
		}
		this.maxSubExecutionName = init.getSettings().getSubExecutions().get(0);
		this.maxLineName = init.getExecutionName() + "_Max";
		this.maxLine = createLsqStdDev(init, maxSubExecutionName, maxLineName);

		this.minSubExecutionName = init.getSettings().getSubExecutions().get(1);
		this.minLineName = init.getExecutionName() + "_Min";
		this.minLine = createLsqStdDev(init, minSubExecutionName, minLineName);

		this.acceptableLineLevel = init.getSettings().getDoubleSetting("L", 0.5).getValue();

		this.acceptableXfrom = init.getSettings().getDoubleSetting("XF", 1.0).getValue();
		this.acceptableXto = init.getSettings().getDoubleSetting("XT", 2.0).getValue();
	}

	private LeastSquaresStraightStdDev createLsqStdDev(StockAlgorithmInit init, String subExecutionName, String name)
			throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.addSubExecutionName(subExecutionName);
		final StockAlgorithmInit newInit = new StockAlgorithmInit(name, init, settings);
		settings.setInteger("N", init.getSettings().getIntegerSetting("N", 9).getValue());
		final LeastSquaresStraightStdDev lsqStdDev = new LeastSquaresStraightStdDev(newInit);
		return lsqStdDev;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		maxLine.process(day);
		minLine.process(day);
	}

	/**
	 * {{@link #processHelper(Day)}
	 * 
	 * @return If there is a cross: Doubles values in next order: a1, a2, b1,
	 *         b2, cross x, cross y If there is no cross: {@link
	 *         Collections.emptyList()}
	 */
	protected List<Double> processHelper(Day day) {
		final SignalContainer<?> maxSignal = getSignal(maxLineName, day.getDate());
		final SignalContainer<?> minSignal = getSignal(minLineName, day.getDate());
		if (maxSignal == null || minSignal == null) {
			return Collections.emptyList();
		}
		final double maxStdDev = maxSignal.getSignal(DoubleSignal.class).getValue();
		final double minStdDev = minSignal.getSignal(DoubleSignal.class).getValue();
		if (maxStdDev >= acceptableLineLevel || minStdDev >= acceptableLineLevel) {
			return Collections.emptyList();
		}
		// y = a0 + a1 * x ; move to:
		// maximum line: y = a1 * x + b1
		// minimum line: y = a2 * x + b2
		final List<Double> maxLineCoef = getSignal(maxLine.getLssName(), day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();
		final List<Double> minLineCoef = getSignal(minLine.getLssName(), day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();
		final double a1 = maxLineCoef.get(1);
		final double a2 = minLineCoef.get(1);
		final double b1 = maxLineCoef.get(0);
		final double b2 = minLineCoef.get(0);

		final double currentX = maxLine.getCurrentX();

		final List<Double> xyCross = getCrossXY(a1, b1, a2, b2);
		final Double crossX = xyCross.get(0);

		if (crossX.isNaN() || !(currentX + acceptableXfrom <= crossX && crossX < currentX + acceptableXto)) {
			return Collections.emptyList();
		}

		final ArrayList<Double> result = new ArrayList<>();
		result.add(a1);
		result.add(a2);
		result.add(b1);
		result.add(b2);
		result.add(xyCross.get(0));
		result.add(xyCross.get(1));
		return result; // a1, a2, b1, b2, cross x, cross y
	}

	public static List<Double> getCrossXY(double a1, double b1, double a2, double b2) {
		// maximum line: y = a1 * x + b1
		// minimum line: y = a2 * x + b2
		try {
			final SimpleMatrix A = new SimpleMatrix(2, 2);
			A.set(0, 0, a1);
			A.set(1, 0, a2);
			A.set(0, 1, -1);
			A.set(1, 1, -1);

			final SimpleMatrix b = new SimpleMatrix(2, 1);
			b.set(0, 0, -b1);
			b.set(1, 0, -b2);

			final SimpleMatrix x = A.solve(b);
			final ArrayList<Double> result = new ArrayList<>();
			result.add(x.get(0, 0));
			result.add(x.get(1, 0));
			return result;
		} catch (SingularMatrixException sme) {
			return Arrays.asList(new Double[] { Double.NaN, Double.NaN });
		}
	}
}
