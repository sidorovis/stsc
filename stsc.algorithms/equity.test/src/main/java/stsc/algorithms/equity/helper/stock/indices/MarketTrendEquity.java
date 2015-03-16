package stsc.algorithms.equity.helper.stock.indices;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import stsc.algorithms.stock.indices.MarketTrend;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Side;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;

public class MarketTrendEquity extends EodAlgorithm {

	private final int positionSharesSize;
	private final String subExecution;

	private final double openLongBorder;
	private final double openShortBorder;
	private final double closeLongBorder;
	private final double closeShortBorder;

	private final Map<String, Integer> longPositions = new HashMap<>();
	private final Map<String, Integer> shortPositions = new HashMap<>();

	public MarketTrendEquity(EodAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		final List<String> subExecutions = init.getSettings().getSubExecutions();
		if (subExecutions.size() < 1) {
			throw new BadAlgorithmException(MarketTrend.class.toString() + " require at least one sub execution parameter");
		}
		subExecution = subExecutions.get(0);
		positionSharesSize = init.getSettings().getIntegerSetting("PSS", 100).getValue();
		openLongBorder = init.getSettings().getDoubleSetting("OLB", 60.0).getValue();
		openShortBorder = init.getSettings().getDoubleSetting("OSB", -60.0).getValue();
		closeLongBorder = init.getSettings().getDoubleSetting("CLB", 40.0).getValue();
		closeShortBorder = init.getSettings().getDoubleSetting("CSB", -40.0).getValue();
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
		return Optional.empty();
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		for (Entry<String, Day> e : datafeed.entrySet()) {
			final String stockName = e.getKey();
			final double v = getSignal(e.getKey(), subExecution, date).getContent(DoubleSignal.class).getValue();
			processStock(stockName, v);
		}
	}

	private void processStock(String stockName, double v) {
		if (longPositions.containsKey(stockName)) {
			if (v < closeLongBorder) {
				broker().sell(stockName, Side.LONG, positionSharesSize);
				longPositions.remove(stockName);
			}
		} else if (shortPositions.containsKey(stockName)) {
			if (v > closeShortBorder) {
				broker().sell(stockName, Side.SHORT, positionSharesSize);
				shortPositions.remove(stockName);
			}
		} else {
			if (v > openLongBorder) {
				broker().buy(stockName, Side.LONG, positionSharesSize);
				longPositions.put(stockName, Integer.valueOf(positionSharesSize));
			} else if (v < openShortBorder) {
				broker().buy(stockName, Side.SHORT, positionSharesSize);
				shortPositions.put(stockName, Integer.valueOf(positionSharesSize));
			}
		}
	}

}
