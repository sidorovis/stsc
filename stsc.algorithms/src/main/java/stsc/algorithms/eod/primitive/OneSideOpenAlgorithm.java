package stsc.algorithms.eod.primitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Side;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;

public class OneSideOpenAlgorithm extends EodAlgorithm {

	final Side side;
	boolean opened = false;

	public OneSideOpenAlgorithm(EodAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		final AlgorithmSetting<String> sideValue = init.getSettings().getStringSetting("side", "long");
		if (sideValue.getValue().compareTo("long") == 0) {
			this.side = Side.LONG;
		} else {
			this.side = Side.SHORT;
		}
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
		return Optional.empty();
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		if (opened)
			return;
		if (datafeed.isEmpty())
			return;

		for (Map.Entry<String, Day> i : datafeed.entrySet()) {
			broker().buy(i.getKey(), side, 100);
		}

		opened = true;
	}
}
