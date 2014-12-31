package stsc.algorithms.stock.patterns;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class WedgePattern extends StockAlgorithm {

	public WedgePattern(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(ListOfDoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		// TODO Auto-generated method stub

	}

}
