package stsc.algorithms.stock.indices.adx;

import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AverageDirectionalMovement extends StockAlgorithm {

	public AverageDirectionalMovement(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class));
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
