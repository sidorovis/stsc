package stsc.algorithms.stock.factors.primitive;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Side;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.SideSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Level extends StockAlgorithm {

	final double level;
	final String factorName;

	public Level(StockAlgorithmInit initialize) throws BadAlgorithmException {
		super(initialize);
		level = Math.abs(initialize.getSettings().getDoubleSetting("f", 0.0).getValue());
		factorName = initialize.getSettings().getSubExecutions().get(0);
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
