package stsc.algorithms.stock.indices.primitive;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class SeveralLastMin extends StockAlgorithm {

	private int currentIndex = 0;
	private final Map<Integer, Double> connections = new HashMap<Integer, Double>();
	private final TreeSet<Double> values = new TreeSet<>((c1, c2) -> {
		return Double.compare(c1, c2);
	});
	private Double lastValue;

	private final Integer N;
	private final String subExecutionName;

	public SeveralLastMin(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 9).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(SeveralLastMin.class + " algorithm require at least one sub algorithms.");
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
		final Double v = getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		if (currentIndex == 0) {
			lastValue = v;
			addSignal(day.getDate(), new DoubleSignal(lastValue));
		}
		values.add(v);
		connections.put(currentIndex, v);
		if (currentIndex - N >= 0) {
			final Double value = connections.remove(currentIndex - N);
			values.remove(value);
		}
		final double smallestValue = values.first();
		if (smallestValue != lastValue) {
			lastValue = smallestValue;
			addSignal(day.getDate(), new DoubleSignal(lastValue));
		}
		currentIndex += 1;
	}
}
