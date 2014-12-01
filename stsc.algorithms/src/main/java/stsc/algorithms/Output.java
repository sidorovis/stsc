package stsc.algorithms;

import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.IntegerSignal;
import stsc.signals.SideSignal;
import stsc.signals.series.CommonSignalsSerie;

public class Output extends StockAlgorithm {

	final String fromExecution;

	public Output(StockAlgorithmInit initialize) throws BadAlgorithmException {
		super(initialize);
		List<String> subExecutions = initialize.getSettings().getSubExecutions();
		if (subExecutions.size() < 1)
			throw new BadAlgorithmException("out algorithm should have at least one sub-execution");
		this.fromExecution = subExecutions.get(0);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(final StockAlgorithmInit init) throws BadAlgorithmException {
		return new CommonSignalsSerie<StockSignal>(DoubleSignal.class);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		Signal<? extends StockSignal> signalHandler = getSignal(fromExecution, day.getDate());
		if (signalHandler == null)
			return;
		final StockSignal signal = signalHandler.getValue();
		if (signal == null)
			return;
		if (signal instanceof DoubleSignal) {
			addSignal(day.getDate(), signal);
		} else if (signal instanceof SideSignal) {
			final SideSignal sideSignal = (SideSignal) signal;
			final DoubleSignal result = new DoubleSignal(sideSignal.getValue() * sideSignal.getSide().value());
			addSignal(day.getDate(), result);
		} else if (signal instanceof IntegerSignal) {
			addSignal(day.getDate(), new DoubleSignal(((IntegerSignal) signal).value));
		}
	}
}
