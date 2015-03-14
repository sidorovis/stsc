package stsc.algorithms.stock.indices.primitive;

import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Side;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
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
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		return Optional.of(new LimitSignalsSerie<SerieSignal>(SideSignal.class));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final DoubleSignal s = getSignal(factorName, day.getDate()).getContent(DoubleSignal.class);
		if (s.getValue() > level)
			addSignal(day.getDate(), new SideSignal(Side.LONG, s.getValue()));
		else if (s.getValue() < -level)
			addSignal(day.getDate(), new SideSignal(Side.SHORT, s.getValue()));
	}
}
