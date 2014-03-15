package stsc.algorithms.primitive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodAlgorithm;
import stsc.common.Day;
import stsc.signals.EodSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

public class TestingEodAlgorithm extends EodAlgorithm {

	public TestingEodAlgorithm(String executionName, Broker broker, SignalsStorage signalsStorage,
			AlgorithmSettings algorithmSettings) {
		super(executionName, broker, signalsStorage, algorithmSettings);
	}

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	@Override
	public Class<? extends EodSignal> registerSignalsClass() {
		return TestingEodAlgorithmSignal.class;
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		datafeeds.add(datafeed);
		DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		TestingEodAlgorithmSignal signal = new TestingEodAlgorithmSignal(fd.format(date));
		addSignal(date, signal);
	}
}
