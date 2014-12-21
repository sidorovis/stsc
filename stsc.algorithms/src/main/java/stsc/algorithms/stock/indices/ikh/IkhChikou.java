package stsc.algorithms.stock.indices.ikh;

import java.util.LinkedList;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class IkhChikou extends StockAlgorithm {

	private final int tm;
	private final LinkedList<Double> closes = new LinkedList<>();

	public IkhChikou(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		tm = init.getSettings().getIntegerSetting("TM", 26).getValue();
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int currentIndex = getCurrentIndex();
		closes.addFirst(day.getPrices().getClose());
		if (currentIndex < tm) {
			addSignal(day.getDate(), new DoubleSignal(closes.getLast()));
		} else {
			final double close = closes.pollLast();
			addSignal(day.getDate(), new DoubleSignal(close));
		}
	}
}
