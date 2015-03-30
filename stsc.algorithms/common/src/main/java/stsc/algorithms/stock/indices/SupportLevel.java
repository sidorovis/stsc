package stsc.algorithms.stock.indices;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;

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

import com.google.common.collect.TreeMultiset;

public class SupportLevel extends StockAlgorithm {

	private final String subExecutionName;

	private final int N;
	private final int M;

	private int currentIndex = 0;
	private double sumOfNMininalValues = 0.0;

	private final LinkedList<Pair<Integer, Double>> elements = new LinkedList<Pair<Integer, Double>>();
	private final TreeMultiset<Pair<Integer, Double>> elementsSortedByMax = TreeMultiset.create(new Comparator<Pair<Integer, Double>>() {
		@Override
		public int compare(Pair<Integer, Double> arg0, Pair<Integer, Double> arg1) {
			return arg1.getRight().compareTo(arg0.getRight());
		}
	});

	public SupportLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() <= 0) {
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
		final Pair<Integer, Double> element = new ImmutablePair<Integer, Double>(currentIndex, value);
		final double averageFromLastNMinValues = getAvFromMins(element);
		addSignal(day.getDate(), new DoubleSignal(averageFromLastNMinValues));
		currentIndex += 1;
	}

	private double getAvFromMins(final Pair<Integer, Double> newE) {
		while (!elements.isEmpty() && elements.getLast().getLeft() < currentIndex - M) {
			final Pair<Integer, Double> v = elements.pollLast();
			elementsSortedByMax.remove(v);
			sumOfNMininalValues -= v.getRight();
		}
		if (elements.isEmpty() || elements.size() < N) {
			elements.addFirst(newE);
			elementsSortedByMax.add(newE);
			sumOfNMininalValues += newE.getRight();
		} else {
			final Pair<Integer, Double> maxValue = elementsSortedByMax.iterator().next();
			if (maxValue.getRight() > newE.getRight()) {
				final Pair<Integer, Double> toDelete = elementsSortedByMax.pollFirstEntry().getElement();
				elements.remove(toDelete);
				sumOfNMininalValues -= toDelete.getRight();
				elements.addFirst(newE);
				elementsSortedByMax.add(newE);
				sumOfNMininalValues += newE.getRight();
			}
		}
		return sumOfNMininalValues / (elements.size());
	}
}
