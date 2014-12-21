package stsc.algorithms.stock.indices.atr;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AtrTrueRange extends StockAlgorithm {

	private Day previousDay;

	public AtrTrueRange(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		if (previousDay == null) {
			previousDay = day;
		}

		final double firstValue = day.getPrices().getHigh() - day.getPrices().getLow();
		final double secondValue = Math.abs(day.getPrices().getHigh() - previousDay.getPrices().getClose());
		final double thirdValue = Math.abs(day.getPrices().getLow() - previousDay.getPrices().getClose());

		final double value = Math.max(firstValue, Math.max(secondValue, thirdValue));
		addSignal(day.getDate(), new DoubleSignal(value));
		previousDay = day;
	}
}
