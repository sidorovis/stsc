package stsc.algorithms.stock.indices.ikh;

import java.util.LinkedList;
import java.util.PriorityQueue;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class IkhPrototype extends StockAlgorithm {

	private int signalIndex = 0;

	private final Integer ts;
	private final Integer tm;

	private final PriorityQueue<Double> highs = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c2, c1);
	});
	private final PriorityQueue<Double> lows = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c1, c2);
	});

	private final LinkedList<Double> lowAtPeriod = new LinkedList<>();
	private final LinkedList<Double> highAtPeriod = new LinkedList<>();

	private final LinkedList<Double> lowBeforePeriod = new LinkedList<>();
	private final LinkedList<Double> highBeforePeriod = new LinkedList<>();

	public IkhPrototype(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.ts = init.getSettings().getIntegerSetting("TS", 9).getValue();
		this.tm = init.getSettings().getIntegerSetting("TM", 26).getValue();
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		if (signalIndex < tm) {
			addLowHighToBefore(day);
			addSignal(day.getDate(), new DoubleSignal((day.getPrices().getLow() + day.getPrices().getHigh()) / 2.0));
		} else if (signalIndex < tm + ts) {
			addLowHighToBefore(day);
			copyLastToAtPeriod();
			addSignal(day.getDate(), new DoubleSignal((highs.peek() + lows.peek()) / 2.0));
		} else {
			addLowHighToBefore(day);
			copyLastToAtPeriod();
			final Double low = lowAtPeriod.pollLast();
			final Double high = highAtPeriod.pollLast();
			lows.remove(low);
			highs.remove(high);
			addSignal(day.getDate(), new DoubleSignal((highs.peek() + lows.peek()) / 2.0));
		}
		signalIndex++;
	}

	private void addLowHighToBefore(Day day) {
		final double lowPrice = day.getPrices().getLow();
		lowBeforePeriod.addFirst(lowPrice);
		final double highPrice = day.getPrices().getHigh();
		highBeforePeriod.addFirst(highPrice);
	}

	private void copyLastToAtPeriod() {
		final Double low = lowBeforePeriod.pollLast();
		lowAtPeriod.addFirst(low);
		lows.add(low);
		final Double high = highBeforePeriod.pollLast();
		highAtPeriod.addFirst(high);
		highs.add(high);
	}

}
