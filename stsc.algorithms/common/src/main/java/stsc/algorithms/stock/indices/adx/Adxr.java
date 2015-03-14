package stsc.algorithms.stock.indices.adx;

import java.util.Optional;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Adxr extends StockAlgorithm {

	private int currentIndex = 0;
	private final Integer N;

	private final String adxAdxName;
	private final AdxAdx adxAdx;

	public Adxr(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		N = init.getSettings().getIntegerSetting("N", 14).getValue();

		this.adxAdxName = init.getExecutionName() + "_AdxAdx";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("size", N + 1);
		settings.setInteger("N", N);
		final StockAlgorithmInit adxAdxInit = new StockAlgorithmInit(adxAdxName, init, settings);
		this.adxAdx = new AdxAdx(adxAdxInit);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("N", 14).getValue().intValue() + 1;
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		adxAdx.process(day);
		final double current = getSignal(adxAdxName, day.getDate()).getContent(DoubleSignal.class).getValue();
		double previous = 0.0;
		if (currentIndex <= N) {
			previous = getSignal(adxAdxName, 0).getContent(DoubleSignal.class).getValue();
		} else {
			previous = getSignal(adxAdxName, currentIndex - N).getContent(DoubleSignal.class).getValue();
		}
		currentIndex += 1;
		addSignal(day.getDate(), new DoubleSignal((current - previous) / 2.0));
	}
}
