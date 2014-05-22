package stsc.algorithms.eod.primitive;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.CommonSignalsSerie;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.SignalsSerie;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;

public class TestingEodAlgorithm extends EodAlgorithm {

	public TestingEodAlgorithm(EodAlgorithm.Init init) throws BadAlgorithmException {
		super(init);
	}

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(Init init) throws BadAlgorithmException {
		return new CommonSignalsSerie<EodSignal>(TestingEodAlgorithmSignal.class);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		datafeeds.add(datafeed);
		final DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		final TestingEodAlgorithmSignal signal = new TestingEodAlgorithmSignal(fd.format(date));
		addSignal(date, signal);
	}
}
