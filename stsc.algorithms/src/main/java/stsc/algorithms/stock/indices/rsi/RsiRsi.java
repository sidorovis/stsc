package stsc.algorithms.stock.indices.rsi;

import java.util.Optional;

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

public class RsiRsi extends StockAlgorithm {

	private final String rsiUname;
	private final RsiU rsiU;

	private final String emaUname;
	private final Ema emaU;

	private final String rsiDname;
	private final RsiD rsiD;

	private final String emaDname;
	private final Ema emaD;

	public RsiRsi(final StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.rsiUname = init.getExecutionName() + "_RsiU";
		this.rsiU = new RsiU(init.createInit(rsiUname));

		this.emaUname = init.getExecutionName() + "_RsiEmaU";
		this.emaU = createUema(init);

		this.rsiDname = init.getExecutionName() + "_RsiD";
		this.rsiD = new RsiD(init.createInit(rsiDname));

		this.emaDname = init.getExecutionName() + "_RsiEmaD";
		this.emaD = createDema(init);
	}

	private Ema createUema(final StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setDouble("P", init.getSettings().getDoubleSetting("P", 0.3).getValue());
		settings.addSubExecutionName(rsiUname);
		return new Ema(init.createInit(emaUname, settings));
	}

	private Ema createDema(final StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setDouble("P", init.getSettings().getDoubleSetting("P", 0.3).getValue());
		settings.addSubExecutionName(rsiDname);
		return new Ema(init.createInit(emaDname, settings));
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(final StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(final Day day) throws BadSignalException {
		rsiU.process(day);
		emaU.process(day);
		rsiD.process(day);
		emaD.process(day);

		final double emaNu = getSignal(emaUname, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double emaNd = getSignal(emaDname, day.getDate()).getContent(DoubleSignal.class).getValue();

		if (Double.compare(0.0, emaNd) == 0) {
			addSignal(day.getDate(), new DoubleSignal(0.0));
		} else {
			final double rs = emaNu / emaNd;
			final double rsi = 100.0 - (100.0 / (1 + rs));
			addSignal(day.getDate(), new DoubleSignal(rsi));
		}
	}
}
