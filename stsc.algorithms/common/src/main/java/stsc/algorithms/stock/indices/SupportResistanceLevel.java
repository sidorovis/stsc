package stsc.algorithms.stock.indices;

import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;

public class SupportResistanceLevel extends StockAlgorithm {

	public SupportResistanceLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		// TODO Auto-generated method stub
		
	}

}
