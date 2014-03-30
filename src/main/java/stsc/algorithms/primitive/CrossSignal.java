package stsc.algorithms.primitive;

import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.AlgorithmSetting;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;

public class CrossSignal extends EodAlgorithm {

	private final AlgorithmSetting<Integer> n = new AlgorithmSetting<Integer>(22);
	private final AlgorithmSetting<Double> ps = new AlgorithmSetting<Double>(100000.0);

	public CrossSignal(Init init) throws BadAlgorithmException {
		super(init);
		init.settings.get("n", n).get("ps", ps);
	}

	@Override
	public void process(final Date date, final HashMap<String, Day> datafeed) throws BadSignalException {
		
	}

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return null;
	}
}
