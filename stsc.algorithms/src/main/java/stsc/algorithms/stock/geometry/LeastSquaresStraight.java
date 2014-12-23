package stsc.algorithms.stock.geometry;

import java.util.LinkedList;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class LeastSquaresStraight extends StockAlgorithm {

	private final int N;
	private final String subExecutionName;

	private double currentX = 0.0;
	private final LinkedList<Double> y = new LinkedList<>();

	private double sumY = 0.0;
	private double sumX = 0.0;
	private double sumSqrX = 0.0;
	private double sumXY = 0.0;

	public LeastSquaresStraight(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresStraight.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(ListOfDoubleSignal.class, size);
		// y = a0 + a1 * x + et
		// double get by index 1 -> a0
		// double get by index 2 -> a1
	}

	@Override
	public void process(Day day) throws BadSignalException {
		currentX += 1;
		final double yValue = getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		y.add(yValue);

		sumY += yValue;
		sumSqrX += (currentX * currentX);
		sumX += currentX;
		sumXY += currentX * yValue;

		final ListOfDoubleSignal signal = new ListOfDoubleSignal();
		if (currentX >= N) {
			final double oldX = currentX - N + 1;
			final double oldY = y.pollFirst();
			sumY -= oldY;
			sumX -= oldX;
			sumSqrX -= (oldX * oldX);
			sumXY -= (oldX * oldY);
			final double divider = (N * sumSqrX - (sumX * sumX));
			if (Double.compare(divider, 0.0) == 0) {
				signal.add(0.0);
				signal.add(0.0);
				addSignal(day.getDate(), signal);
			} else {
				final double a0 = (sumY * sumSqrX - sumX * sumXY) / divider;
				final double a1 = (N * sumXY - sumY * sumX) / divider;
				signal.add(a0);
				signal.add(a1);
				addSignal(day.getDate(), signal);
			}
		} else {
			signal.add(0.0);
			signal.add(0.0);
			addSignal(day.getDate(), signal);
		}

	}

}
