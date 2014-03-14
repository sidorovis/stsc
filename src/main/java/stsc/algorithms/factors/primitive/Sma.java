package stsc.algorithms.factors.primitive;

import java.util.LinkedList;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockSignal;
import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public class Sma extends StockAlgorithm {

	final int n = 5;

	public Sma(String stockName, String executionName, SignalsStorage signalsStorage, AlgorithmSettings algorithmSettings) {
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
		if (elements.size() == n) {
			addSignal(day.getDate(), new DoubleSignal(sum / n));
		} else if (elements.size() > n) {
			Double lastElement = elements.pollLast();
			sum -= lastElement;
			addSignal(day.getDate(), new DoubleSignal(sum / n));
		}
	}
}
