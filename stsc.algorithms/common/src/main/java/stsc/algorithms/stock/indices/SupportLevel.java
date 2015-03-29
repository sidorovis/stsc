package stsc.algorithms.stock.indices;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class SupportLevel extends StockAlgorithm {

	private final String subExecutionName;

	private final int N;
	private final int M;

	private int currentIndex = 0;

	private final TreeSet<Double> lastValuesByMax = new TreeSet<Double>();

	private final PriorityQueue<Pair<Integer, Double>> lastMins = new PriorityQueue<>(0, new Comparator<Pair<Integer, Double>>() {
		@Override
		public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
			return o1.getLeft().compareTo(o2.getLeft());
		}
	});

	public SupportLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() > 0) {
			throw new BadAlgorithmException("sub executions settings for " + SupportLevel.class.toString()
					+ " should have at least one algorithm");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
		this.N = init.getSettings().getIntegerSetting("N", 8).getValue();
		this.M = init.getSettings().getIntegerSetting("M", 66).getValue();

	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double value = getSignal(subExecutionName, day.getDate()).getContent(DoubleSignal.class).getValue();
		pushElement(value);

		currentIndex += 1;
	}

	private void pushElement(double value) {

		final Pair<Integer, Double> newPair = new ImmutablePair<Integer, Double>(currentIndex, value);
	}

	private void pollElement() {
		final Pair<Integer, Double> v = lastMins.poll();

	}

}
