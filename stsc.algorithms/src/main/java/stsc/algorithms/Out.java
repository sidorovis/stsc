package stsc.algorithms;

import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.CommonSignalsSerie;

public class Out extends StockAlgorithm {

	final String fromExecution;

	public Out(StockAlgorithmInit initialize) throws BadAlgorithmException {
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
		addSignal(day.getDate(), getSignal(fromExecution, day.getDate()).getValue());
	}
}
