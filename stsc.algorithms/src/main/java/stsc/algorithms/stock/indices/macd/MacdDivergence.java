package stsc.algorithms.stock.indices.macd;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class MacdDivergence extends StockAlgorithm {

	private final String macdSignalName;
	private final MacdSignal macdSignal;
	private final String macdName;

	public MacdDivergence(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);

		if (init.getSettings().getSubExecutions().size() < 1) {
			throw new BadAlgorithmException(MacdDivergence.class + " should have at least one sub on stock algorithm");
		}

		this.macdSignalName = init.getExecutionName() + "_MacdSignal";
		this.macdSignal = new MacdSignal(init.createInit(macdSignalName));
		this.macdName = macdSignal.getMacdName();
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		macdSignal.process(day);

		final double macd = getSignal(macdName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double signal = getSignal(macdSignalName, day.getDate()).getContent(DoubleSignal.class).getValue();

		addSignal(day.getDate(), new DoubleSignal(macd - signal));
	}
}
