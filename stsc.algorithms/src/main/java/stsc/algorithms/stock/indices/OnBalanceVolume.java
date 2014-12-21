package stsc.algorithms.stock.indices;

import java.util.LinkedList;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class OnBalanceVolume extends StockAlgorithm {

	private int index = 0;

	private final int N;
	private LinkedList<Double> closes = new LinkedList<>();
	private LinkedList<Double> obv = new LinkedList<>();

	public OnBalanceVolume(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.N = init.getSettings().getIntegerSetting("N", 5).getValue();
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double pt = day.getPrices().getClose();
		final double volume = day.getVolume();
		closes.addLast(pt);
		if (index == 0) {
			addSignal(day.getDate(), new DoubleSignal(0.0));
			obv.add(0.0);
		} else if (index < N) {
			final double previousObv = obv.getFirst();
			final double newObv = createObv(pt, closes.getFirst(), volume);
			obv.add(previousObv + newObv);
			addSignal(day.getDate(), new DoubleSignal(previousObv + newObv));
		} else {
			final double previousObv = obv.pollFirst();
			final double newObv = createObv(pt, closes.pollFirst(), volume);
			obv.add(previousObv + newObv);
			addSignal(day.getDate(), new DoubleSignal(previousObv + newObv));
		}
		index += 1;
	}

	private double createObv(double pt, double ptn, double volume) {
		double n = 0.0;
		if (pt > ptn) {
			n = volume;
		} else if (pt < ptn) {
			n = -volume;
		}
		return n;
	}

}
