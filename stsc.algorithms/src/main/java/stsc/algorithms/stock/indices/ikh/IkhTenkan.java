package stsc.algorithms.stock.indices.ikh;

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

public class IkhTenkan extends StockAlgorithm {

	private final String prototypeName;
	private final IkhPrototype prototype;

	public IkhTenkan(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.prototypeName = init.getExecutionName() + "_IhkPrototype";
		final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
		settings.setInteger("TS", init.getSettings().getIntegerSetting("TS", 9).getValue());
		settings.setInteger("TM", 0);
		this.prototype = new IkhPrototype(init.createInit(prototypeName, settings));
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		prototype.process(day);
		final DoubleSignal v = getSignal(prototypeName, day.getDate()).getContent(DoubleSignal.class);
		addSignal(day.getDate(), v);
	}
}
