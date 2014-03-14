package stsc.algorithms.factors.primitive;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockSignal;
import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.storage.SignalsStorage.Handler;

public class Ema extends StockAlgorithm {

	final String smaExecutionName = "sma#1";

	public Ema(String stockName, String executionName, SignalsStorage signalsStorage, AlgorithmSettings algorithmSettings) {
		super(stockName, executionName, signalsStorage, algorithmSettings);
		algorithmSettings.get("smaExecutionName", smaExecutionName);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return DoubleSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int signalIndex = getCurrentIndex();
		Handler<? extends StockSignal> smaSignal = getSignal(smaExecutionName, signalIndex);
		if (smaSignal != null) {
			
		}
	}

}
