package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.StockSignal;

public class TestingStockAlgorithm extends StockAlgorithm {

	public TestingStockAlgorithm(StockAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass() {
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
