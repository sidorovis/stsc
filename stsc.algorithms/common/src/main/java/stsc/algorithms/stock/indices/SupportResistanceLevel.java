package stsc.algorithms.stock.indices;

import java.util.Optional;
import java.util.PriorityQueue;

import org.apache.commons.lang3.tuple.Pair;

import stsc.algorithms.stock.indices.primitive.SmStDev;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class SupportResistanceLevel extends StockAlgorithm {

	private final String subExecutionName;

	private final int N;
	private final int M;
	
	private final PriorityQueue<Pair<Integer, Double>> lastMins = new PriorityQueue<>(initialCapacity, comparator)

	public SupportResistanceLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() > 0) {
			throw new BadAlgorithmException("sub executions settings for " + SupportResistanceLevel.class.toString()
					+ " should have at least one algorithm");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
		this.N = init.getSettings().getIntegerSetting("N", 8).getValue();
		this.M = init.getSettings().getIntegerSetting("M", 66).getValue();

		// SmStDev
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		// TODO Auto-generated method stub

	}

}
