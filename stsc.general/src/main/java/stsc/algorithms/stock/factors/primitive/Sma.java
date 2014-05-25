package stsc.algorithms.stock.factors.primitive;

import java.util.LinkedList;
import java.util.List;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.LimitSignalsSerie;
import stsc.algorithms.SignalsSerie;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmInit;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.StockSignal;

public class Sma extends StockAlgorithm {

	private final String subAlgoName;
	private final AlgorithmSetting<Integer> n = new AlgorithmSetting<>(Integer.valueOf(5));

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
		final AlgorithmSetting<Integer> size = new AlgorithmSetting<Integer>(2);
		initialize.settings.getInteger("size", size);
		return new LimitSignalsSerie<StockSignal>(DoubleSignal.class, size.getValue());
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
