package stsc.algorithms.stock.geometry;

import java.util.LinkedList;
import java.util.List;

import stsc.algorithms.ListOfDoubleAdapter;
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
	private final List<Double> y = new LinkedList<>();

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
		// double get by index 0 -> probability
		// double get by index 1 -> angle
	}

	@Override
	public void process(Day day) throws BadSignalException {
		currentX += 1;
		final double yValue = getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		y.add(yValue);
		final ListOfDoubleSignal signal = new ListOfDoubleSignal();
		if (currentX == N) {
			
		} else if (currentX > N) {

		} else {
			signal.add(0.0);
			signal.add(0.0);
			addSignal(day.getDate(), signal);
		}

	}

}
