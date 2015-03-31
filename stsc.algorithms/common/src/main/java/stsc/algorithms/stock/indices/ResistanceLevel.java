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

public class ResistanceLevel extends StockAlgorithm {

	private static final class PairComparator implements Comparator<Pair<Integer, Double>> {
		@Override
		public int compare(Pair<Integer, Double> arg0, Pair<Integer, Double> arg1) {
			if (arg1.getRight().compareTo(arg0.getRight()) == 0) {
				return arg1.getLeft().compareTo(arg0.getLeft());
			} else
				return arg1.getRight().compareTo(arg0.getRight());
		}
	};

	private static final PairComparator PAIR_COMPARATOR = new PairComparator();

	private final String subExecutionName;

	private final int N;
	private final int M;

	private int currentIndex = 0;
	private double sumOfNMaximalValues = 0.0;

	private final LinkedList<Pair<Integer, Double>> elements = new LinkedList<Pair<Integer, Double>>();

	// by second value, order: 27, 24, 20, 17, 12, 8, 5, 3, 1
	private final TreeMultiset<Pair<Integer, Double>> mElementsSorted = TreeMultiset.create(PAIR_COMPARATOR);
	private final TreeMultiset<Pair<Integer, Double>> nElementsSorted = TreeMultiset.create(PAIR_COMPARATOR);

	public ResistanceLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() <= 0) {
			throw new BadAlgorithmException("sub executions settings for " + ResistanceLevel.class.toString()
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
		final double averageFromLastNMaxValues = getAvFromMaxs(element);
		addSignal(day.getDate(), new DoubleSignal(averageFromLastNMaxValues));
		currentIndex += 1;
	}

	private double getAvFromMaxs(final Pair<Integer, Double> newE) {
		while (!elements.isEmpty() && elements.getLast().getLeft() < currentIndex - M) {
			final Pair<Integer, Double> v = elements.pollLast();
			if (nElementsSorted.contains(v)) {
				nElementsSorted.remove(v);
				if (!mElementsSorted.isEmpty()) {
					final Pair<Integer, Double> el = mElementsSorted.pollFirstEntry().getElement();
					sumOfNMaximalValues += el.getRight();
					nElementsSorted.add(el);
				}
				sumOfNMaximalValues -= v.getRight();
			} else if (mElementsSorted.contains(v)) {
				mElementsSorted.remove(v);
			}
		}
		if (nElementsSorted.size() < N) {
			elements.addFirst(newE);
			nElementsSorted.add(newE);
			sumOfNMaximalValues += newE.getRight();
		} else {
			elements.addFirst(newE);
			final double minValue = nElementsSorted.lastEntry().getElement().getRight();
			if (newE.getRight() > minValue) {
				nElementsSorted.add(newE);
				final Pair<Integer, Double> v = nElementsSorted.pollLastEntry().getElement();
				mElementsSorted.add(v);
				sumOfNMaximalValues += newE.getRight();
				sumOfNMaximalValues -= v.getRight();
			} else {
				mElementsSorted.add(newE);
			}
		}
		return sumOfNMaximalValues / (nElementsSorted.size());
	}
}
