package stsc.algorithms.eod.primitive;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.LimitSignalsSerie;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.EodSignal;
import stsc.signals.SignalsSerie;

public class TestingEodAlgorithm extends EodAlgorithm {

	public TestingEodAlgorithm(EodAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
	}

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(Init init) throws BadAlgorithmException {
		final int size = init.settings.getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<EodSignal>(TestingEodAlgorithmSignal.class, size);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		datafeeds.add(datafeed);
		final DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		final TestingEodAlgorithmSignal signal = new TestingEodAlgorithmSignal(fd.format(date));
		addSignal(date, signal);
	}
}
