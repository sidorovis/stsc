package stsc.algorithms.primitive;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.EodAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.EodSignal;

public class TestingEodAlgorithm extends EodAlgorithm {

	public TestingEodAlgorithm(EodAlgorithm.Init init) {
		super(init, TestingEodAlgorithm.class);
	}

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return TestingEodAlgorithmSignal.class;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		datafeeds.add(datafeed);
		final DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		final TestingEodAlgorithmSignal signal = new TestingEodAlgorithmSignal(fd.format(date));
		addSignal(date, signal);
	}
}
