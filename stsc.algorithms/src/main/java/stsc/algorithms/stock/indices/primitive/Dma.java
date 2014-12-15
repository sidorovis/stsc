package stsc.algorithms.stock.indices.primitive;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Dma extends StockAlgorithm {

	private final Double P;
	private final String emaName;
	private final Ema ema;

	public Dma(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.P = init.getSettings().getDoubleSetting("P", 0.2).getValue();
		this.emaName = init.getExecutionName() + "_Ema";
		this.ema = new Ema(init.createInit(emaName));
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		ema.process(day);
		final double emaValue = getSignal(emaName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final int signalIndex = getCurrentIndex();
		if (signalIndex == 0) {
			addSignal(day.getDate(), new DoubleSignal(emaValue));
		} else {
			final double previous = getSignal(signalIndex - 1).getSignal(DoubleSignal.class).getValue();
			final double value = P * emaValue + (1 - P) * previous;
			addSignal(day.getDate(), new DoubleSignal(value));
		}
	}
}
