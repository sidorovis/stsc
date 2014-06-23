package stsc.general.simulator.multistarter.genetic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class AlgorithmSettingsGeneticListTest extends TestCase {

	public void testAlgorithmSettingsGeneticList() throws ParseException, BadParameterException, BadAlgorithmException {
		final FromToPeriod period = TestHelper.getPeriod();
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory(period);
		factory.add(new MpInteger("q", -20, 100, 1));
		factory.add(new MpInteger("w", -40, 15, 1));
		factory.add(new MpDouble("a", -60.0, 100.0, 3.0));
		factory.add(new MpDouble("s", -100.0, 101.0, 4.0));
		factory.add(new MpString("z", Arrays.asList(new String[] { "asd", "ibm", "yhoo" })));
		factory.add(new MpString("z", Arrays.asList(new String[] { "vokrug", "fileName" })));
		factory.add(new MpSubExecution("p", Arrays.asList(new String[] { "12313-432423", "234535-23424", "35345-234234135", "24454-65462245" })));
		final AlgorithmSettingsGeneticList mas = factory.getGeneticList();

		final Set<String> codes = new HashSet<>();
		final int TEST_SIZE = 500000;
		while (codes.size() < TEST_SIZE) {
			for (int i = 0; i < TEST_SIZE; ++i) {
				final StringBuilder b = new StringBuilder();
				mas.generateRandom().stringHashCode(b);
				codes.add(b.toString());
			}
		}
		assertEquals(true, codes.size() >= TEST_SIZE);
	}
}
