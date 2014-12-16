package stsc.algorithms.stock.indices.macd;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MacdSignal extends StockAlgorithm {

	private final String macdMacdName;
	private final MacdMacd macd;

	private final String smaName;
	private final Sma sma;

	public MacdSignal(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);

		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(MacdSignal.class + " should have at least one sub on stock algorithm");
		}

		this.macdMacdName = init.getExecutionName() + "_Macd";
		this.macd = createMacd(init);

		this.smaName = init.getExecutionName() + "_Sma";
		this.sma = createSma(init);
	}

	private MacdMacd createMacd(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("S", init.getSettings().getIntegerSetting("S", 12).getValue());
		settings.setInteger("L", init.getSettings().getIntegerSetting("L", 26).getValue());
		settings.getSubExecutions().addAll(init.getSettings().getSubExecutions());
		return new MacdMacd(init.createInit(macdMacdName, settings));
	}

	private Sma createSma(StockAlgorithmInit init) throws BadAlgorithmException {
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("N", init.getSettings().getIntegerSetting("A", 9).getValue());
		settings.addSubExecutionName(macdMacdName);
		return new Sma(init.createInit(smaName, settings));
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		macd.process(day);
		sma.process(day);

		addSignal(day.getDate(), getSignal(smaName, day.getDate()).getValue());
	}
}
