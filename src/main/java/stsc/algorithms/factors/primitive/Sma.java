package stsc.algorithms.factors.primitive;

import java.util.LinkedList;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.DoubleSignal;
import stsc.signals.StockSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public class Sma extends StockAlgorithm {

	private final AlgorithmSetting<Integer> n = new AlgorithmSetting<>(new Integer(5));

	public Sma(String stockName, String executionName, SignalsStorage signalsStorage,
			AlgorithmSettings algorithmSettings) {
		super(stockName, executionName, signalsStorage, algorithmSettings);
		algorithmSettings.get("n", n);
	}

	final LinkedList<Double> elements = new LinkedList<>();
	Double sum = new Double(0.0);

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return DoubleSignal.class;
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double price = day.prices.getOpen();
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
