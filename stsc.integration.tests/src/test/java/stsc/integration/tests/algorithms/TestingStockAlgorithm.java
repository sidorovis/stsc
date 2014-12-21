package stsc.integration.tests.algorithms;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.signals.series.CommonSignalsSerie;

public final class TestingStockAlgorithm extends StockAlgorithm {

	public TestingStockAlgorithm(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public CommonSignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit init) {
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
