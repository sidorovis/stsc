package stsc.algorithms.eod.primitive;

import java.util.HashMap;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.EodPosition;
import stsc.algorithms.SignalsSerie;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;
import stsc.trading.Side;

public class SimpleTradingAlgorithm extends EodAlgorithm {

	Date boughtDate = null;
	final HashMap<String, EodPosition> openedPositions = new HashMap<String, EodPosition>();

	public SimpleTradingAlgorithm(EodAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		if (openedPositions.isEmpty()) {
			buy(datafeed);
		} else {
			checkStatus(date, datafeed);
		}
	}

	private void buy(HashMap<String, Day> datafeed) {
		int toBuy = 20;
		int boughtStocks = 0;
		for (Map.Entry<String, Day> i : datafeed.entrySet()) {
			String stockName = i.getKey();
			int boughtAmount = broker().buy(stockName, Side.LONG, 500);
			if (boughtAmount > 0) {
				boughtDate = i.getValue().getDate();
				boughtStocks += 1;
				openedPositions.put(stockName, new EodPosition(stockName, Side.LONG, boughtAmount));
			}
			if (boughtStocks == toBuy) {
				break;
			}
		}
	}

	private void checkStatus(Date date, HashMap<String, Day> datafeed) {
		Days daysDiff = Days.daysBetween(new DateTime(boughtDate), new DateTime(date));
		if (daysDiff.getDays() > 15) {
			sell(datafeed);
		}
	}

	private void sell(HashMap<String, Day> datafeed) {
		HashSet<String> positionKeysToDelete = new HashSet<String>();
		for (Map.Entry<String, EodPosition> i : openedPositions.entrySet()) {
			EodPosition p = i.getValue();
			int allSharesAmount = p.getSharedAmount();
			int soldAmount = broker().sell(i.getKey(), p.getSide(), allSharesAmount);
			p.setSharedAmount(allSharesAmount - soldAmount);
			if (allSharesAmount == soldAmount)
				positionKeysToDelete.add(p.getStockName());
		}
		for (String string : positionKeysToDelete) {
			openedPositions.remove(string);
		}
	}

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(Init init) throws BadAlgorithmException {
		return null;
	}

}
