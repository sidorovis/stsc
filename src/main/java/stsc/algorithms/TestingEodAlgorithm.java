package stsc.algorithms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.storage.BadSignalException;

public class TestingEodAlgorithm extends EodAlgorithm {

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	public TestingEodAlgorithm() {
	}

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return TestingAlgorithmSignal.class;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) {
		datafeeds.add(datafeed);
		DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		TestingAlgorithmSignal signal = new TestingAlgorithmSignal(fd.format(date));
		try {
			addSignal(date, signal);
		} catch (BadSignalException e) {
			// do nothing
		}
	}
}
