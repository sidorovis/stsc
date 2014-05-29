package stsc.algorithms.eod.primitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import stsc.algorithms.AlgorithmSettingImpl;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Side;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.EodSignal;
import stsc.common.signals.SignalsSerie;

public class OneSideOpenAlgorithm extends EodAlgorithm {

	final AlgorithmSetting<String> side = new AlgorithmSettingImpl<String>("long");
	boolean opened = false;

	public OneSideOpenAlgorithm(EodAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		init.getSettings().get("side", side);
	}

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
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
