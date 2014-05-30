package stsc.integration.tests.algorithms.eod.privitive;

import java.util.HashMap;
import java.util.Date;

import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import junit.framework.TestCase;

public class SimpleTradingAlgorithmTest extends TestCase {
	public void testTestingEodAlgorithm() throws BadSignalException, BadAlgorithmException {

		EodAlgoInitHelper init = new EodAlgoInitHelper("eName");

		TestingEodAlgorithm tea = new TestingEodAlgorithm(init.getInit());
		tea.process(new Date(), new HashMap<String, Day>());
	}
}
