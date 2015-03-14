package stsc.algorithms.stock.indices.primitive;

import java.util.List;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Diff extends StockAlgorithm {

	final String fromExecution;
	final String toExecution;

	public Diff(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		List<String> subExecutions = init.getSettings().getSubExecutions();
		if (subExecutions.size() < 2)
			throw new BadAlgorithmException("diff algorithm should have at least two subalgorithms");
		fromExecution = subExecutions.get(0);
		toExecution = subExecutions.get(1);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<SerieSignal>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		SignalContainer<? extends SerieSignal> from = getSignal(fromExecution, day.getDate());
		SignalContainer<? extends SerieSignal> to = getSignal(toExecution, day.getDate());
		if (from != null && to != null) {
			final double fromValue = from.getContent(DoubleSignal.class).getValue();
			final double toValue = to.getContent(DoubleSignal.class).getValue();
			addSignal(day.getDate(), new DoubleSignal(fromValue - toValue));
		}
	}
}
