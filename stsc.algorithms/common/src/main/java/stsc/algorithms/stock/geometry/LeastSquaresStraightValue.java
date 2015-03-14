package stsc.algorithms.stock.geometry;

import java.util.LinkedList;
import java.util.Optional;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class LeastSquaresStraightValue extends StockAlgorithm {

	private final int N;
	private final String subExecutionName;

	private double currentX = 0.0;
	private final LinkedList<Double> y = new LinkedList<>();

	private double sumY = 0.0;
	private double sumX = 0.0;
	private double sumXX = 0.0;
	private double sumXY = 0.0;

	public LeastSquaresStraightValue(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresStraightValue.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(ListOfDoubleSignal.class, size));
		// y = a0 + a1 * x
		// double get by index 0 -> a0
		// double get by index 1 -> a1
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final SignalContainer<? extends SerieSignal> signal = getSignal(subExecutionName, day.getDate());
		if (signal == null) {
			return;
		}
		final double yValue = signal.getContent(DoubleSignal.class).getValue();
		y.addLast(yValue);
		addValues(currentX, yValue);
		if (currentX >= N) {
			final double oldX = currentX - N;
			final double oldY = y.pollFirst();
			deleteValues(oldX, oldY);
		}
		calculateSignalWithMatrix(day);
		currentX += 1;
	}

	private void addValues(double newX, double newY) {
		sumY += newY;
		sumXX += (newX * newX);
		sumX += newX;
		sumXY += newX * newY;
	}

	private void deleteValues(double oldX, double oldY) {
		sumY -= oldY;
		sumX -= oldX;
		sumXX -= (oldX * oldX);
		sumXY -= (oldX * oldY);
	}

	private void calculateSignalWithMatrix(Day day) throws BadSignalException {

		try {
			final SimpleMatrix A = new SimpleMatrix(2, 2);
			A.set(0, 0, sumXX);
			A.set(1, 0, sumX);
			A.set(0, 1, sumX);
			A.set(1, 1, y.size());

			final SimpleMatrix b = new SimpleMatrix(2, 1);
			b.set(0, 0, sumXY);
			b.set(1, 0, sumY);

			final SimpleMatrix x = A.solve(b);
			final ListOfDoubleSignal signal = new ListOfDoubleSignal();
			signal.add(x.get(1, 0));
			signal.add(x.get(0, 0));
			addSignal(day.getDate(), signal);
		} catch (SingularMatrixException sme) {
			setParallelXsignal(day);
		}
	}

	private void setParallelXsignal(Day day) throws BadSignalException {
		final ListOfDoubleSignal signal = new ListOfDoubleSignal();
		signal.add(y.getLast());
		signal.add(0.0);
		addSignal(day.getDate(), signal);
	}

}
