package stsc.algorithms.primitive;

import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.StockSignal;

public class TestingStockAlgorithm extends StockAlgorithm {

	public TestingStockAlgorithm(StockAlgorithm.Init init) {
		super(init, TestingStockAlgorithm.class);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
