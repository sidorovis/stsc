package stsc.algorithms.stock.factors.primitive;

import java.util.List;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage.Handler;

public class Ema extends StockAlgorithm {

	private final String subAlgoName;
	private final AlgorithmSetting<Double> P = new AlgorithmSetting<Double>(0.2);

	public Ema(final StockAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
		init.settings.get("P", P);
		List<String> subExecutionNames = init.settings.getSubExecutions();
		if (subExecutionNames.size() < 1)
			throw new BadAlgorithmException("sub executions parameters not enought");
		subAlgoName = subExecutionNames.get(0);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return DoubleSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int signalIndex = getCurrentIndex();
		final double price = getSignal(subAlgoName, day.getDate()).getSignal(DoubleSignal.class).value;
		if (signalIndex == 0) {
			addSignal(day.getDate(), new DoubleSignal(price));
		} else {
			final Handler<? extends StockSignal> previousEmaSignal = getSignal(signalIndex - 1);
			if (previousEmaSignal != null) {
				final double previousEmaValue = previousEmaSignal.getSignal(DoubleSignal.class).value;
				final double value = P.getValue() * price + (1.0 - P.getValue()) * previousEmaValue;
				addSignal(day.getDate(), new DoubleSignal(value));
			}
		}
	}
}
