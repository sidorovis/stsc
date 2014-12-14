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

public class MinForNDays extends StockAlgorithm {

	private int signalIndex = 0;

	private final Integer period;
	private final Integer sleepagePeriod;

	private final String subExecutionName;

	private final PriorityQueue<Double> lows = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c1, c2);
	});
	private final LinkedList<Double> lowAtPeriod = new LinkedList<>();
	private final LinkedList<Double> lowBeforePeriod = new LinkedList<>();

	public MinForNDays(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.period = init.getSettings().getIntegerSetting("P", 5).getValue();
		this.sleepagePeriod = init.getSettings().getIntegerSetting("SP", 5).getValue();

		final List<String> subExecutions = init.getSettings().getSubExecutions();
		if (subExecutions.size() < 1) {
			throw new BadAlgorithmException("Algorithm " + MinForNDays.class.toString() + " require 1 sub execution");
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
			addSignal(day.getDate(), new DoubleSignal(lows.peek()));
		} else {
			copyLastToAtPeriod();
			final Double high = lowAtPeriod.pollLast();
			lows.remove(high);
			addSignal(day.getDate(), new DoubleSignal(lows.peek()));
		}
		signalIndex++;
	}

	private void addLowHighToBefore(double v) {
		lowBeforePeriod.addFirst(v);
	}

	private void copyLastToAtPeriod() {
		final Double low = lowBeforePeriod.pollLast();
		lowAtPeriod.addFirst(low);
		lows.add(low);
	}

}
