package stsc.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.trading.Broker;

public class TestAlgorithm implements Algorithm {

	public ArrayList<HashMap<String, Day>> datafeeds = new ArrayList<HashMap<String, Day>>();

	public TestAlgorithm() {
	}

	@Override
	public void setBroker(Broker broker) {
		// do nothing
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) {
		datafeeds.add(datafeed);
	}

}
