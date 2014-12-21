package stsc.algorithms.stock.indices.adx;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdxDxi extends StockAlgorithm {

	private final String adxSmaDiName;
	private final AdxSmaDi adxSmaDi;

	public AdxDxi(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		final Integer N = init.getSettings().getIntegerSetting("N", 14).getValue();

		this.adxSmaDiName = init.getExecutionName() + "_adxSmaDiName";
		this.adxSmaDi = createAdxSmaDi(N, init);
	}

	private AdxSmaDi createAdxSmaDi(Integer N, StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", N);
		final StockAlgorithmInit adxSmaDiInit = new StockAlgorithmInit(adxSmaDiName, init, settings);
		return new AdxSmaDi(adxSmaDiInit);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		adxSmaDi.process(day);
		final ListOfDoubleSignal adxSmaDiSignal = getSignal(adxSmaDiName, day.getDate()).getSignal(ListOfDoubleSignal.class);
		final double diMinus = adxSmaDiSignal.getValues().get(0);
		final double diPlus = adxSmaDiSignal.getValues().get(1);
		if (Double.compare(diPlus + diMinus, 0.0) == 0) {
			addSignal(day.getDate(), new DoubleSignal(100.0));
		} else {
			final double dxi = 100 * Math.abs(diPlus - diMinus) / (diPlus + diMinus);
			addSignal(day.getDate(), new DoubleSignal(dxi));
		}
	}
}
