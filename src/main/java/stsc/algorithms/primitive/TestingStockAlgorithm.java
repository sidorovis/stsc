package stsc.algorithms.primitive;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.StockSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public class TestingStockAlgorithm extends StockAlgorithm {

	public TestingStockAlgorithm(String stockName, String executionName, SignalsStorage signalsStorage,
			AlgorithmSettings algorithmSettings) {
		super(stockName, executionName, signalsStorage, algorithmSettings);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return null;
	}

	@Override
	public void process(Day day) throws BadSignalException {
	}

}
