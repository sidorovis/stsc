package stsc.integration.tests.algorithms.eod.privitive;

import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.integration.tests.helper.EodAlgoInitHelper;

public class SimpleTradingAlgorithmTest {

	@Test
	public void testTestingEodAlgorithm() throws BadSignalException, BadAlgorithmException {
		final EodAlgoInitHelper init = new EodAlgoInitHelper("eName");

		final TestingEodAlgorithm tea = new TestingEodAlgorithm(init.getInit());
		tea.process(new Date(), new HashMap<String, Day>());
	}
}
