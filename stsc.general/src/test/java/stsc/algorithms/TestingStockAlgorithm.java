package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.signals.StockSignal;

public final class TestingStockAlgorithm extends StockAlgorithm {

	public TestingStockAlgorithm(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public CommonSignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit init) {
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
