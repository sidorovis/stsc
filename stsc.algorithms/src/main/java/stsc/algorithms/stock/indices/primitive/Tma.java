package stsc.algorithms.stock.indices.primitive;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Tma extends StockAlgorithm {

	private final Double P;
	private final String dmaName;
	private final Dma dma;

	public Tma(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.P = init.getSettings().getDoubleSetting("P", 0.2).getValue();
		this.dmaName = init.getExecutionName() + "_Dma";
		this.dma = new Dma(init.createInit(dmaName));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<SerieSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		dma.process(day);
		final double dmaValue = getSignal(dmaName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final int signalIndex = getCurrentIndex();
		if (signalIndex == 0) {
			addSignal(day.getDate(), new DoubleSignal(dmaValue));
		} else {
			final double previous = getSignal(signalIndex - 1).getContent(DoubleSignal.class).getValue();
			final double value = P * dmaValue + (1 - P) * previous;
			addSignal(day.getDate(), new DoubleSignal(value));
		}
	}
}
