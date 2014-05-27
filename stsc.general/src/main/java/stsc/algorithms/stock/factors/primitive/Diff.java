package stsc.algorithms.stock.factors.primitive;

import java.util.List;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.LimitSignalsSerie;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmInit;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.signals.DoubleSignal;
import stsc.signals.Signal;
import stsc.signals.SignalsSerie;
import stsc.signals.StockSignal;

public class Diff extends StockAlgorithm {

	final String fromExecution;
	final String toExecution;

	public Diff(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		List<String> subExecutions = init.settings.getSubExecutions();
		if (subExecutions.size() < 2)
			throw new BadAlgorithmException("diff algorithm should have at least two subalgorithms");
		fromExecution = subExecutions.get(0);
		toExecution = subExecutions.get(1);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.settings.getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		Signal<? extends StockSignal> from = getSignal(fromExecution, day.getDate());
		Signal<? extends StockSignal> to = getSignal(toExecution, day.getDate());
		if (from != null && to != null) {
			final double fromValue = from.getSignal(DoubleSignal.class).value;
			final double toValue = to.getSignal(DoubleSignal.class).value;
			addSignal(day.getDate(), new DoubleSignal(fromValue - toValue));
		}
	}
}
