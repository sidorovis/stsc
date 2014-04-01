package stsc.algorithms.factors.primitive;

import java.util.List;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage.Handler;

public class Diff extends StockAlgorithm {

	final String fromExecution;
	final String toExecution;

	public Diff(Init init) throws BadAlgorithmException {
		super(init);
		List<String> subExecutions = init.settings.getSubExecutions();
		if (subExecutions.size() < 2)
			throw new BadAlgorithmException("diff algorithm should have at least two subalgorithms");
		fromExecution = subExecutions.get(0);
		toExecution = subExecutions.get(1);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return DoubleSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		Handler<? extends StockSignal> from = getSignal(fromExecution, day.getDate());
		Handler<? extends StockSignal> to = getSignal(toExecution, day.getDate());
		if (from != null && to != null) {
			final double fromValue = from.getSignal(DoubleSignal.class).value;
			final double toValue = to.getSignal(DoubleSignal.class).value;
			addSignal(day.getDate(), new DoubleSignal(fromValue - toValue));
		}
	}
}
