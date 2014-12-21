package stsc.algorithms.eod.primitive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.series.LimitSignalsSerie;

public class TestingEodAlgorithm extends EodAlgorithm {

	public TestingEodAlgorithm(EodAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
		final int size = init.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<SerieSignal>(TestingEodAlgorithmSignal.class, size);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		datafeeds.add(datafeed);
		final DateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		final TestingEodAlgorithmSignal signal = new TestingEodAlgorithmSignal(fd.format(date));
		addSignal(date, signal);
	}
}
