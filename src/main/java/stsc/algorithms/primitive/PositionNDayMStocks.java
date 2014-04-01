package stsc.algorithms.primitive;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodPosition;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.signals.EodSignal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage.Handler;

/*
 * PositionNDayMStocks open position for n days on m stocks in long and short sides
 * 
 */
public class PositionNDayMStocks extends EodAlgorithm {

	private final AlgorithmSetting<Integer> n = new AlgorithmSetting<Integer>(22);
	private final AlgorithmSetting<Integer> m = new AlgorithmSetting<Integer>(2);
	private final AlgorithmSetting<Double> ps = new AlgorithmSetting<Double>(100000.0);
	private final String factorExecutionName;

	class Factor implements Comparable<Factor> {
		Double factor;
		String stockName;

		public Factor(Double factor, String stockName) {
			super();
			this.factor = factor;
			this.stockName = stockName;
		}

		public int compareTo(Factor o) {
			return factor.compareTo(o.factor);
		}

		@Override
		public String toString() {
			DecimalFormat decimalFormatter = new DecimalFormat("#0.00");
			return stockName + ":" + decimalFormatter.format(factor);
		}

	}

	private final ArrayList<Factor> sortedStocks = new ArrayList<>();
	private final HashMap<String, EodPosition> openPositions = new HashMap<>();

	public PositionNDayMStocks(Init init) throws BadAlgorithmException {
		super(init);
		init.settings.get("n", n);
		init.settings.get("ps", ps);
		init.settings.get("m", m);
		final List<String> subExecutions = init.settings.getSubExecutions();
		if (subExecutions.size() < 1)
			throw new BadAlgorithmException(
					"CrossSignal algorithm should receive one stock based execution with Double");
		factorExecutionName = subExecutions.get(0);
	}

	@Override
	public void process(final Date date, final HashMap<String, Day> datafeed) throws BadSignalException {
		if (openPositions.isEmpty()) {
			open(date, datafeed);
		} else {
		}

		sortedStocks.clear();
	}

	private void open(final Date date, final HashMap<String, Day> datafeed) {
		sortedStocks.clear();
		for (Map.Entry<String, Day> i : datafeed.entrySet()) {
			String stockName = i.getKey();
			Handler<? extends StockSignal> signal = getSignal(stockName, factorExecutionName, date);
			if (signal != null)
				sortedStocks.add(new Factor(signal.getSignal(DoubleSignal.class).value, stockName));
		}
		Collections.sort(sortedStocks);

		if (sortedStocks.size() < m.getValue() * 2) {
			return;
		}

	}

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return null;
	}
}
