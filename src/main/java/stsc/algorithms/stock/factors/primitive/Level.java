package stsc.algorithms.stock.factors.primitive;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.SideSignal;
import stsc.signals.StockSignal;
import stsc.trading.Side;

public class Level extends StockAlgorithm {

	final double level;
	final String factorName;

	public Level(Init initialize) throws BadAlgorithmException {
		super(initialize);
		AlgorithmSetting<Double> factorLevel = new AlgorithmSetting<Double>(0.0);
		initialize.settings.get("f", factorLevel);
		level = Math.abs(factorLevel.getValue());
		factorName = initialize.settings.getSubExecutions().get(0);
	}

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return SideSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final DoubleSignal s = getSignal(factorName, day.getDate()).getSignal(DoubleSignal.class);
		if (s.value > level)
			addSignal(day.getDate(), new SideSignal(Side.LONG, s.value));
		else if (s.value < -level)
			addSignal(day.getDate(), new SideSignal(Side.SHORT, s.value));
	}
}
