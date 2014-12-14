package stsc.algorithms.stock.indices.ikh;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class IkhSenkauA extends StockAlgorithm {

	private final String tenkanName;
	private final IkhPrototype tenkan;

	private final String kijunName;
	private final IkhPrototype kijun;

	public IkhSenkauA(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		final int ts = init.getSettings().getIntegerSetting("TS", 9).getValue();
		final int tm = init.getSettings().getIntegerSetting("TM", 26).getValue();
		{
			this.tenkanName = init.getExecutionName() + "_IhkTenkan";
			final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
			settings.setInteger("TS", ts);
			settings.setInteger("TM", tm);
			this.tenkan = new IkhPrototype(init.createInit(tenkanName, settings));
		}
		{
			this.kijunName = init.getExecutionName() + "_IhkKijun";
			final AlgorithmSettingsImpl settings = new AlgorithmSettingsImpl(init);
			settings.setInteger("TS", tm);
			settings.setInteger("TM", tm);
			this.kijun = new IkhPrototype(init.createInit(kijunName, settings));
		}
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		tenkan.process(day);
		kijun.process(day);
		final double vTenkan = getSignal(tenkanName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		final double vKijun = getSignal(kijunName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		addSignal(day.getDate(), new DoubleSignal((vTenkan + vKijun) / 2.0));
	}
}
