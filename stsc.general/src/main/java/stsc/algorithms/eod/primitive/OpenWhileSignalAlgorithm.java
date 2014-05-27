package stsc.algorithms.eod.primitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodPosition;
import stsc.algorithms.SignalsSerie;
import stsc.common.Day;
import stsc.common.Side;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;
import stsc.signals.SideSignal;
import stsc.signals.Signal;
import stsc.signals.StockSignal;

public class OpenWhileSignalAlgorithm extends EodAlgorithm {

	private final HashMap<String, EodPosition> shortPositions = new HashMap<>();
	private final HashMap<String, EodPosition> longPositions = new HashMap<>();

	private double P;
	private String sideSignalAlgoName;

	public OpenWhileSignalAlgorithm(Init init) throws BadAlgorithmException {
		super(init);
		AlgorithmSetting<Double> p = new AlgorithmSetting<Double>(10000.0);
		init.settings.get("P", p);
		this.P = p.getValue();
		if (init.settings.getSubExecutions().isEmpty())
			throw new BadAlgorithmException("Open While Signal Algorithm should receive at least one sub-algorithm");
		this.sideSignalAlgoName = init.settings.getSubExecutions().get(0);
	}

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(Init init) throws BadAlgorithmException {
		return null;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {

		for (Map.Entry<String, Day> i : datafeed.entrySet()) {
			final String stockName = i.getKey();
			final Signal<? extends StockSignal> isSignal = getSignal(stockName, sideSignalAlgoName, date);
			if (isSignal == null) {
				if (shortPositions.containsKey(stockName)) {
					broker().sell(stockName, Side.SHORT, shortPositions.get(stockName).getSharedAmount());
					shortPositions.remove(stockName);
				} else if (longPositions.containsKey(stockName)) {
					broker().sell(stockName, Side.LONG, longPositions.get(stockName).getSharedAmount());
					longPositions.remove(stockName);
				}
			} else {
				final SideSignal ss = isSignal.getSignal(SideSignal.class);
				final Side signalSide = ss.getSide();
				if (signalSide == Side.LONG && !longPositions.containsKey(stockName)) {
					final int sharesSize = getSharesSize(i.getValue().getPrices().getOpen());
					longPositions.put(stockName, new EodPosition(stockName, Side.LONG, sharesSize));
					broker().buy(stockName, Side.LONG, sharesSize);
				} else if (signalSide == Side.SHORT && !shortPositions.containsKey(stockName)) {
					final int sharesSize = getSharesSize(i.getValue().getPrices().getOpen());
					shortPositions.put(stockName, new EodPosition(stockName, Side.SHORT, sharesSize));
					broker().buy(stockName, Side.SHORT, sharesSize);
				}
			}
		}
	}

	private int getSharesSize(final Double price) {
		return (int) Math.round(P / price);
	}
}
