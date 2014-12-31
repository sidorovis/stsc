package stsc.algorithms.stock.indices.primitive;

import java.util.LinkedList;

import stsc.algorithms.stock.geometry.LeastSquaresQuadraticValue;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class SeveralLastMax extends StockAlgorithm {

	private int currentIndex = 0;
	private final LinkedList<Integer> maxListIndex = new LinkedList<>();
	private final LinkedList<Double> maxList = new LinkedList<>();

	private final String subExecutionName;

	public SeveralLastMax(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(LeastSquaresQuadraticValue.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double v = getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		if (maxList.isEmpty()) {
			maxListIndex.addFirst(currentIndex);
			maxList.addFirst(v);
		}
		final double lastV = maxList.getLast();
		final double lastIndex = maxListIndex.getLast();
		if (lastIndex + 1 == currentIndex) {
			if (v > lastV) {
				maxList.pollLast();
				maxList.addLast(v);
				maxListIndex.pollLast();
				maxListIndex.addLast(currentIndex);
			} else if (Double.compare(v, lastV) == 0) {
				maxListIndex.pollLast();
				maxListIndex.addLast(currentIndex);
			} else {
				addSignal(day.getDate(), new DoubleSignal(lastV));
			}
		}

		if (lastIndex + 1 < currentIndex) {
			if (v >= lastV) {
				maxList.addLast(v);
				maxListIndex.addLast(currentIndex);
			} else {
				addSignal(day.getDate(), new DoubleSignal(lastV));
			}
		}

		if (maxList.size() > 1) {
			maxList.pollFirst();
			maxListIndex.pollFirst();
		}

		currentIndex += 1;
	}
}
