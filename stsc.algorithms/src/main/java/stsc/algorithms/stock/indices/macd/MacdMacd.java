package stsc.algorithms.stock.indices.macd;

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

public class MacdMacd extends StockAlgorithm {

	private final int s;
	private final int l;

	private final String emaSname;
	private final Ema emaS;

	private final String emaLname;
	private final Ema emaL;

	public MacdMacd(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);

		this.s = init.getSettings().getIntegerSetting("S", 12).getValue();
		this.l = init.getSettings().getIntegerSetting("L", 26).getValue();

		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(MacdMacd.class + " should have at least one sub on stock algorithm");
		}

		this.emaSname = init.getExecutionName() + "_EmaS";
		this.emaS = createEma(emaSname, init, s);

		this.emaLname = init.getExecutionName() + "_EmaL";
		this.emaL = createEma(emaLname, init, l);
	}

	private Ema createEma(String name, StockAlgorithmInit init, int length) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setDouble("P", 2.0 / (1.0 + length));
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		return new Ema(init.createInit(name, settings));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		emaS.process(day);
		emaL.process(day);

		final double sValue = getSignal(emaSname, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double lValue = getSignal(emaLname, day.getDate()).getContent(DoubleSignal.class).getValue();

		addSignal(day.getDate(), new DoubleSignal(sValue - lValue));
	}
}
