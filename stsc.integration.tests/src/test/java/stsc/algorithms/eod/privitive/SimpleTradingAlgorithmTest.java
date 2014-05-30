package stsc.algorithms.eod.privitive;

import java.util.HashMap;
import java.util.Date;

import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class SimpleTradingAlgorithmTest extends TestCase {
	public void testTestingEodAlgorithm() throws BadSignalException, BadAlgorithmException {

		final EodAlgorithmInit init = TestAlgorithmsHelper.getEodAlgorithmInit();

		TestingEodAlgorithm tea = new TestingEodAlgorithm(init);
		tea.process(new Date(), new HashMap<String, Day>());
	}
}
