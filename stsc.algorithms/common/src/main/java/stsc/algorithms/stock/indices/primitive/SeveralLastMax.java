package stsc.algorithms.stock.indices.primitive;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
	private final Map<Integer, Double> connections = new HashMap<Integer, Double>();
	private final TreeMap<Double, Integer> values = new TreeMap<>((c1, c2) -> {
		return Double.compare(c2, c1);
	});
	private Double lastValue;

	private final Integer N;
	private final String subExecutionName;

	public SeveralLastMax(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 9).getValue();
		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(SeveralLastMax.class + " algorithm require at least one sub algorithms.");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final Double v = getSignal(subExecutionName, day.getDate()).getContent(DoubleSignal.class).getValue();
		if (currentIndex == 0) {
			lastValue = v;
			addSignal(day.getDate(), new DoubleSignal(lastValue));
		}
		addValue(v);
		if (currentIndex - N >= 0) {
			removeValue();
		}
		final double smallestValue = values.firstEntry().getKey();
		if (smallestValue != lastValue) {
			lastValue = smallestValue;
			addSignal(day.getDate(), new DoubleSignal(lastValue));
		}
		currentIndex += 1;
	}

	private void removeValue() {
		final Double value = connections.remove(currentIndex - N);
		final Integer i = values.get(value);
		if (i == 1) {
			values.remove(value);
		} else {
			values.put(value, i - 1);
		}
	}

	private void addValue(Double v) {
		connections.put(currentIndex, v);
		final Integer i = values.get(v);
		if (i == null) {
			values.put(v, 1);
		} else {
			values.put(v, i + 1);
		}
	}
}
