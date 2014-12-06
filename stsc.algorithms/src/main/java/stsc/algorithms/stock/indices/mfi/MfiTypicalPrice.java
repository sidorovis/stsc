package stsc.algorithms.stock.indices.mfi;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MfiTypicalPrice extends StockAlgorithm {

	public MfiTypicalPrice(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double typicalPrice = (day.getPrices().getHigh() + day.getPrices().getLow() + day.getPrices().getClose()) / 3;
		addSignal(day.getDate(), new DoubleSignal(typicalPrice));
	}

}
