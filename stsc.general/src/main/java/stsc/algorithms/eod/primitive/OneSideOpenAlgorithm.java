package stsc.algorithms.eod.primitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;
import stsc.trading.Side;

public class OneSideOpenAlgorithm extends EodAlgorithm {

	final AlgorithmSetting<String> side = new AlgorithmSetting<String>("long");
	boolean opened = false;

	public OneSideOpenAlgorithm(Init init) throws BadAlgorithmException {
		super(init);
		init.settings.get("side", side);
	}

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return null;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		if (opened)
			return;
		if (datafeed.isEmpty())
			return;

		Side s = Side.SHORT;
		if (side.getValue().compareTo("long") == 0) {
			s = Side.LONG;
		}
		for (Map.Entry<String, Day> i : datafeed.entrySet()) {
			broker().buy(i.getKey(), s, 100);
		}

		opened = true;
	}
}
