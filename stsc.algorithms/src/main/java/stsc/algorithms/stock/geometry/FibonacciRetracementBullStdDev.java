package stsc.algorithms.stock.geometry;

import java.util.List;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class FibonacciRetracementBullStdDev extends StockAlgorithm {

	private final static double ratios[] = { 0.0, 0.236068, 0.381966, 0.618034, 1.0 };

	private final String subAlgoName;

	public FibonacciRetracementBullStdDev(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		List<String> subExecutionNames = init.getSettings().getSubExecutions();
		if (subExecutionNames.size() < 1)
			throw new BadAlgorithmException(FibonacciRetracementBullStdDev.class.toString() + " require one sub parameter");
		subAlgoName = subExecutionNames.get(0);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 5).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final Optional<DoubleSignal> lastSignal = getSignal(subAlgoName, day.getDate()).getSignal(DoubleSignal.class);
		if (!lastSignal.isPresent()) {
			return;
		}
		final int currentIndex = getCurrentIndex();
		if (currentIndex < ratios.length) {
			addSignal(day.getDate(), new DoubleSignal(Double.MAX_VALUE));
			return;
		}
		final Optional<DoubleSignal> firstSignal = getSignal(subAlgoName, currentIndex - ratios.length).getSignal(DoubleSignal.class);
		final double lastValue = lastSignal.get().getValue();
		final double firstValue = firstSignal.get().getValue();
		if (lastValue < firstValue) {
			addSignal(day.getDate(), new DoubleSignal(Double.MAX_VALUE));
			return;
		}
		final double difference = (lastValue - firstValue);
		double stdDev = 0.0;
		for (int i = 1; i < ratios.length - 1; ++i) {
			final int index = currentIndex - ratios.length + i;
			final double expectedValue = firstValue - ratios[i] * difference;
			final double actualValue = getSignal(subAlgoName, index).getSignal(DoubleSignal.class).get().getValue();
			stdDev += Math.sqrt(actualValue - expectedValue);
		}
		addSignal(day.getDate(), new DoubleSignal(stdDev));
	}
}
