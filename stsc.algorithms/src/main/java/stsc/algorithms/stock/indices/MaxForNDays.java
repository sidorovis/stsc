package stsc.algorithms.stock.indices;

import java.util.LinkedList;
import java.util.List;
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

public class MaxForNDays extends StockAlgorithm {

	private int signalIndex = 0;

	private final Integer period;
	private final Integer sleepagePeriod;

	private final String subExecutionName;

	private final PriorityQueue<Double> highs = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c2, c1);
	});
	private final LinkedList<Double> highAtPeriod = new LinkedList<>();
	private final LinkedList<Double> highBeforePeriod = new LinkedList<>();

	public MaxForNDays(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.period = init.getSettings().getIntegerSetting("P", 5).getValue();
		this.sleepagePeriod = init.getSettings().getIntegerSetting("SP", 5).getValue();

		final List<String> subExecutions = init.getSettings().getSubExecutions();
		if (subExecutions.size() < 1) {
			throw new BadAlgorithmException("Algorithm " + MaxForNDays.class.toString() + " require 1 sub execution");
		}
		this.subExecutionName = subExecutions.get(0);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double dayValue = getSignal(subExecutionName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		addLowHighToBefore(dayValue);
		if (signalIndex < sleepagePeriod) {
			addSignal(day.getDate(), new DoubleSignal(dayValue));
		} else if (signalIndex < sleepagePeriod + period) {
			copyLastToAtPeriod();
			addSignal(day.getDate(), new DoubleSignal(highs.peek()));
		} else {
			copyLastToAtPeriod();
			final Double high = highAtPeriod.pollLast();
			highs.remove(high);
			addSignal(day.getDate(), new DoubleSignal(highs.peek()));
		}
		signalIndex++;
	}

	private void addLowHighToBefore(double v) {
		highBeforePeriod.addFirst(v);
	}

	private void copyLastToAtPeriod() {
		final Double high = highBeforePeriod.pollLast();
		highAtPeriod.addFirst(high);
		highs.add(high);
	}

}
