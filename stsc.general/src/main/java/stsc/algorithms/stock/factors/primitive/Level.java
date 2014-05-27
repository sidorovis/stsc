package stsc.algorithms.stock.factors.primitive;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.LimitSignalsSerie;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmInit;
import stsc.common.Day;
import stsc.common.Side;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.SideSignal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsSerie;

public class Level extends StockAlgorithm {

	final double level;
	final String factorName;

	public Level(StockAlgorithmInit initialize) throws BadAlgorithmException {
		super(initialize);
		AlgorithmSetting<Double> factorLevel = new AlgorithmSetting<Double>(0.0);
		initialize.settings.get("f", factorLevel);
		level = Math.abs(factorLevel.getValue());
		factorName = initialize.settings.getSubExecutions().get(0);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		return new LimitSignalsSerie<StockSignal>(SideSignal.class);
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
