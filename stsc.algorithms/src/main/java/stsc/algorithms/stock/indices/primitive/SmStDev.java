package stsc.algorithms.stock.indices.primitive;

import java.util.LinkedList;
import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class SmStDev extends StockAlgorithm {

	private final String subAlgoName;
	private final String subAlgoNameSma;
	private final AlgorithmSetting<Integer> N;

	private final LinkedList<Double> elements = new LinkedList<>();
	private Double sum = 0.0;

	public SmStDev(final StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		N = init.getSettings().getIntegerSetting("N", 5);
		final List<String> subExecutionNames = init.getSettings().getSubExecutions();
		if (subExecutionNames.size() < 2)
			throw new BadAlgorithmException("Sma algorithm should receive at least one sub algorithm");
		subAlgoName = subExecutionNames.get(0);
		subAlgoNameSma = subExecutionNames.get(1);
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<SerieSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double price = getSignal(subAlgoName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double sma = getSignal(subAlgoNameSma, day.getDate()).getContent(DoubleSignal.class).getValue();
		final double sqr = Math.pow(sma - price, 2);
		sum += sqr;
		elements.push(sqr);
		if (elements.size() <= N.getValue()) {
			final double sqrt = Math.sqrt(sum / elements.size());
			addSignal(day.getDate(), new DoubleSignal(sqrt));
		} else if (elements.size() > N.getValue()) {
			final Double lastElement = elements.pollLast();
			sum -= lastElement;
			final double sqrt = Math.sqrt(sum / elements.size());
			addSignal(day.getDate(), new DoubleSignal(sqrt));
		}
	}
}
