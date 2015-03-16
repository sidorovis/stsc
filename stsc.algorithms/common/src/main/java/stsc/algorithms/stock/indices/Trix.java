package stsc.algorithms.stock.indices;

import java.util.Optional;

import stsc.algorithms.stock.indices.primitive.Tma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Trix extends StockAlgorithm {

	private final String tmaName;
	private final Tma tma;

	public Trix(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.tmaName = init.getExecutionName() + "_Tma";
		this.tma = new Tma(init.createInit(tmaName));
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<SerieSignal>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		tma.process(day);
		final int signalIndex = getIndexForCurrentStock();
		if (signalIndex == 0) {
			addSignal(day.getDate(), new DoubleSignal(0.0));
		} else {
			final double prevoiusTmaValue = getSignal(tmaName, signalIndex - 1).getContent(DoubleSignal.class).getValue();
			final double tmaValue = getSignal(tmaName, day.getDate()).getContent(DoubleSignal.class).getValue();
			if (Double.compare(prevoiusTmaValue, 0.0) == 0) {
				addSignal(day.getDate(), new DoubleSignal(50.0));
			} else {
				final double value = 100.0 * (tmaValue - prevoiusTmaValue) / prevoiusTmaValue;
				addSignal(day.getDate(), new DoubleSignal(value));
			}
		}
	}

}
