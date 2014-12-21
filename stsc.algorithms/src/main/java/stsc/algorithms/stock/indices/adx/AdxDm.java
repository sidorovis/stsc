package stsc.algorithms.stock.indices.adx;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdxDm extends StockAlgorithm {

	private Day previousDay = null;

	public AdxDm(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(ListOfDoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		if (previousDay == null) {
			previousDay = day;
		}
		double dmMinus = previousDay.getPrices().getLow() - day.getPrices().getLow();
		double dmPlus = day.getPrices().getHigh() - previousDay.getPrices().getHigh();

		if (dmMinus < dmPlus || dmMinus < 0) {
			dmMinus = 0.0;
		}
		if (dmPlus < dmMinus || dmPlus < 0) {
			dmPlus = 0;
		}
		addSignal(day.getDate(), new ListOfDoubleSignal().add(dmMinus).add(dmPlus));

		previousDay = day;
	}
}
