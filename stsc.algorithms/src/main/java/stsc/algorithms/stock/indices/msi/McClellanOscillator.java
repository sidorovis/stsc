package stsc.algorithms.stock.indices.msi;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.stock.indices.primitive.Ema;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class McClellanOscillator extends StockAlgorithm {

	final private String slowEmaName;
	final private Ema slowEma;

	final private String fastEmaName;
	final private Ema fastEma;

	public McClellanOscillator(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		final Double pSlow = init.getSettings().getDoubleSetting("slowP", 0.1).getValue();
		final Double pFast = init.getSettings().getDoubleSetting("fastP", 0.05).getValue();

		this.slowEmaName = init.getExecutionName() + "_SlowEma";
		this.slowEma = createEma(slowEmaName, pSlow, init);

		this.fastEmaName = init.getExecutionName() + "_FastEma";
		this.fastEma = createEma(fastEmaName, pFast, init);
	}

	private Ema createEma(String emaName, Double p, StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setDouble("P", p);
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		final StockAlgorithmInit emaInit = init.createInit(emaName, settings);
		return new Ema(emaInit);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		slowEma.process(day);
		fastEma.process(day);

		final double slowV = getSignal(slowEmaName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final double fastV = getSignal(fastEmaName, day.getDate()).getSignal(DoubleSignal.class).getValue();

		addSignal(day.getDate(), new DoubleSignal(slowV - fastV));
	}

}
