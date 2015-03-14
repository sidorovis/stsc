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

public class LeastSquaresQuadraticValue extends StockAlgorithm {

	private final int N;
	private final String subExecutionName;

	private double currentX = 0.0;
	private final LinkedList<Double> y = new LinkedList<>();

	public LeastSquaresQuadraticValue(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresQuadraticValue.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(ListOfDoubleSignal.class, size));
		// y = a0 + a1 * x + a2 * x ^ 2
		// double get by index 0 -> a0
		// double get by index 1 -> a1
		// double get by index 2 -> a2
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final SignalContainer<? extends SerieSignal> signal = getSignal(subExecutionName, day.getDate());
		if (signal == null) {
			return;
		}
		final double yValue = signal.getContent(DoubleSignal.class).getValue();
		y.addLast(yValue);
		if (currentX >= N) {
			y.pollFirst();
		}
		calculateSignal(day);
		currentX += 1;
	}

	private void calculateSignal(Day day) throws BadSignalException {
		double sumXXXX = 0.0;
		double sumXXX = 0.0;
		double sumXX = 0.0;
		double sumXXY = 0.0;
		double sumX = 0.0;
		double sumXY = 0.0;
		double sumY = 0.0;
		double xV = Math.max(0.0, currentX - N + 1);
		for (Double yV : y) {
			sumXXXX += Math.pow(xV, 4);
			sumXXX += Math.pow(xV, 3);
			sumXX += Math.pow(xV, 2);
			sumX += Math.pow(xV, 1);
			sumXXY += Math.pow(xV, 2) * yV;
			sumXY += xV * yV;
			sumY += yV;
			xV += 1;
		}
		try {
			final SimpleMatrix A = new SimpleMatrix(3, 3);
			A.set(0, 0, sumXXXX);
			A.set(0, 1, sumXXX);
			A.set(0, 2, sumXX);

			A.set(1, 0, sumXXX);
			A.set(1, 1, sumXX);
			A.set(1, 2, sumX);

			A.set(2, 0, sumXX);
			A.set(2, 1, sumX);
			A.set(2, 2, y.size());

			final SimpleMatrix b = new SimpleMatrix(3, 1);
			b.set(0, 0, sumXXY);
			b.set(1, 0, sumXY);
			b.set(2, 0, sumY);

			final SimpleMatrix x = A.solve(b);
			final ListOfDoubleSignal signal = new ListOfDoubleSignal();
			signal.add(x.get(2, 0));
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
		signal.add(0.0);
		addSignal(day.getDate(), signal);
	}
}
