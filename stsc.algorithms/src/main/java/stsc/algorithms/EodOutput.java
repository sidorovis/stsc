package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.CommonSignalsSerie;

public class EodOutput extends EodAlgorithm {

	final String fromExecution;

	public EodOutput(EodAlgorithmInit initialize) throws BadAlgorithmException {
		super(initialize);
		List<String> subExecutions = initialize.getSettings().getSubExecutions();
		if (subExecutions.size() < 1)
			throw new BadAlgorithmException("out algorithm should have at least one sub-execution");
		this.fromExecution = subExecutions.get(0);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(final EodAlgorithmInit init) throws BadAlgorithmException {
		return new CommonSignalsSerie<>(DoubleSignal.class);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		SignalContainer<? extends SerieSignal> signalHandler = getSignal(fromExecution, date);
		if (signalHandler == null)
			return;
		final Optional<? extends SerieSignal> signal = signalHandler.getValue();
		if (!signal.isPresent())
			return;
		if (signal.get() instanceof DoubleSignal) {
			addSignal(date, signal.get());
		}
	}
}
