package stsc.algorithms.stock.indices.adx;

import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.common.stocks.Prices;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdxTrueRange extends StockAlgorithm {

	private Day previousDay = null;

	public AdxTrueRange(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		if (previousDay == null) {
			previousDay = day;
		}
		final double max = Math.max(p(day).getHigh(), p(previousDay).getClose());
		final double min = Math.min(p(day).getLow(), p(previousDay).getClose());
		final double trueRange = max - min;
		addSignal(day.getDate(), new DoubleSignal(trueRange));
		previousDay = day;
	}

	private Prices p(Day day) {
		return day.getPrices();
	}

}
