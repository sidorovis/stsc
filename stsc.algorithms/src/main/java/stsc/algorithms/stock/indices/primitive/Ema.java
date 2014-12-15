package stsc.algorithms.stock.indices.primitive;

import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

//@formatter:off
/**
 * Ema on-stock algorithm calculate EMA function over necessary input require:
 * 1) setting for P: EMA[x+1] = PRICE_VALUE * P + EMA[x] * (1 - P); (0.2 by default);
 * 2) size setting that limit result serie; (2 by default);
 * 3) sub on-stock algorithm name (for example In(e=open)).
 */
//@formatter:on
public class Ema extends StockAlgorithm {

	private final String subAlgoName;
	private final AlgorithmSetting<Double> P;

	public Ema(final StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		P = init.getSettings().getDoubleSetting("P", 0.2);
		List<String> subExecutionNames = init.getSettings().getSubExecutions();
		if (subExecutionNames.size() < 1)
			throw new BadAlgorithmException("sub executions parameters not enought");
		subAlgoName = subExecutionNames.get(0);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(final StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int signalIndex = getCurrentIndex();
		final double price = getSignal(subAlgoName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		if (signalIndex == 0) {
			addSignal(day.getDate(), new DoubleSignal(price));
		} else {
			final Signal<? extends StockSignal> previousEmaSignal = getSignal(signalIndex - 1);
			if (previousEmaSignal != null) {
				final double previousEmaValue = previousEmaSignal.getSignal(DoubleSignal.class).getValue();
				final double value = P.getValue() * price + (1.0 - P.getValue()) * previousEmaValue;
				addSignal(day.getDate(), new DoubleSignal(value));
			}
		}
	}
}
