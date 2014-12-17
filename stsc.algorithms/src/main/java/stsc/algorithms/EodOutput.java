package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.EodSignal;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.eod.EodDoubleSignal;
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
	public SignalsSerie<EodSignal> registerSignalsClass(final EodAlgorithmInit init) throws BadAlgorithmException {
		return new CommonSignalsSerie<>(EodDoubleSignal.class);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		Signal<? extends EodSignal> signalHandler = getSignal(fromExecution, date);
		if (signalHandler == null)
			return;
		final EodSignal signal = signalHandler.getValue();
		if (signal == null)
			return;
		if (signal instanceof EodDoubleSignal) {
			addSignal(date, signal);
		}
	}
}
