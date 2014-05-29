package stsc.algorithms.stock.factors.primitive;

import java.util.LinkedList;
import java.util.List;

import stsc.algorithms.AlgorithmSettingImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class Sma extends StockAlgorithm {

	private final String subAlgoName;
	private final AlgorithmSetting<Integer> n = new AlgorithmSettingImpl<>(Integer.valueOf(5));

	final LinkedList<Double> elements = new LinkedList<>();
	Double sum = Double.valueOf(0.0);

	public Sma(final StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		init.settings.get("n", n);
		List<String> subExecutionNames = init.settings.getSubExecutions();
		if (subExecutionNames.size() < 1)
			throw new BadAlgorithmException("Sma algorithm should receive at least one sub algorithm");
		subAlgoName = subExecutionNames.get(0);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.settings.getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double price = getSignal(subAlgoName, day.getDate()).getSignal(DoubleSignal.class).value;
		elements.push(price);
		sum += price;
		if (elements.size() <= n.getValue()) {
			addSignal(day.getDate(), new DoubleSignal(sum / elements.size()));
		} else if (elements.size() > n.getValue()) {
			Double lastElement = elements.pollLast();
			sum -= lastElement;
			addSignal(day.getDate(), new DoubleSignal(sum / n.getValue()));
		}
	}
}
