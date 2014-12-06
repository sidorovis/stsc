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

public class Mfi extends StockAlgorithm {

	private final int N;
	private int currentIndex = 0;

	
	
	public Mfi(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 20).getValue();
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, Math.max(size, N));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double typicalPrice = (day.getPrices().getHigh() + day.getPrices().getLow() + day.getPrices().getClose()) / 3;
		final double moneyFlow = typicalPrice * day.getVolume();
		currentIndex++;
	}

}
