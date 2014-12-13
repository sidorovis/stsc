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
import stsc.common.stocks.Prices;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class IkhTenkan extends StockAlgorithm {

	private final Integer ts;
	private final PriorityQueue<Double> highs = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c2, c1);
	});
	private final PriorityQueue<Double> lows = new PriorityQueue<>((c1, c2) -> {
		return Double.compare(c1, c2);
	});
	private final LinkedList<Double> lowElements = new LinkedList<>();
	private final LinkedList<Double> highElements = new LinkedList<>();

	public IkhTenkan(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		this.ts = init.getSettings().getIntegerSetting("TS", 9).getValue();
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final int index = getCurrentIndex();
		addHighLowPrices(day.getPrices());
		if (index >= ts) {
			final Double low = lowElements.pollFirst();
			final Double high = highElements.pollFirst();
			lows.remove(low);
			highs.remove(high);
		}
		addSignal(day.getDate(), new DoubleSignal((highs.peek() + lows.peek()) / 2.0));
	}

	private void addHighLowPrices(Prices prices) {
		final Double low = prices.getLow();
		final Double high = prices.getHigh();
		lows.add(low);
		highs.add(high);
		lowElements.addLast(low);
		highElements.addLast(high);
	}
}
