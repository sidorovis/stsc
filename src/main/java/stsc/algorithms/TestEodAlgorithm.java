package stsc.algorithms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.ExecutionSignal;

public class TestEodAlgorithm extends EodAlgorithm {

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	public TestEodAlgorithm() {
	}

	@Override
	public Class<? extends ExecutionSignal> registerSignalsClass() {
		return TestAlgorithmSignal.class;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) {
		datafeeds.add(datafeed);
		DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		TestAlgorithmSignal signal = new TestAlgorithmSignal(fd.format(date));
		try {
			addSignal(date, signal);
		} catch (BadSignalException e) {
			// do nothing
		}
	}
}
