package stsc.algorithms.factors.primitive;

import java.util.LinkedList;
import java.util.List;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.StockSignal;

public class Sma extends StockAlgorithm {

	private final String subAlgoName;
	private final AlgorithmSetting<Integer> n = new AlgorithmSetting<>(new Integer(5));

	public Sma(final StockAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
		init.settings.get("n", n);
		List<String> subExecutionNames = init.settings.getSubExecutions();
		if (subExecutionNames.size() < 1)
			throw new BadAlgorithmException("sub executions parameters not enought");
		subAlgoName = subExecutionNames.get(0);
	}

	final LinkedList<Double> elements = new LinkedList<>();
	Double sum = new Double(0.0);

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return DoubleSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double price = getSignal(subAlgoName, day.getDate()).getSignal(DoubleSignal.class).value;
		elements.push(price);
		sum += price;
		if (elements.size() == n.getValue()) {
			addSignal(day.getDate(), new DoubleSignal(sum / n.getValue()));
		} else if (elements.size() > n.getValue()) {
			Double lastElement = elements.pollLast();
			sum -= lastElement;
			addSignal(day.getDate(), new DoubleSignal(sum / n.getValue()));
		}
	}
}
