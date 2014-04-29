package stsc.simulator;

import java.util.Arrays;

import stsc.algorithms.AlgorithmSettings;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.MpDouble;
import stsc.simulator.multistarter.MpInteger;
import stsc.simulator.multistarter.MpString;
import stsc.simulator.multistarter.MpSubExecution;
import stsc.simulator.multistarter.AlgorithmSettingsGridIterator;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class ExecutionInitializerTest extends TestCase {
	public void testExecutionInitializer() throws BadParameterException {
		final AlgorithmSettingsGridIterator mas = new AlgorithmSettingsGridIterator(TestHelper.getPeriod());
		mas.add(new MpInteger("n", 1, 10, 2));
		mas.add(new MpDouble("d", 0.1, 1.0, 0.2));
		final ExecutionInitializer ei = new ExecutionInitializer("e", "a", mas);
		int count = 0;
		for (AlgorithmSettings algorithmSettings : ei) {
			assertNotNull(algorithmSettings);
			count += 1;
		}
		assertEquals(25, count);
		ei.reset();
		for (AlgorithmSettings algorithmSettings : ei) {
			assertNotNull(algorithmSettings);
			count += 1;
		}
		assertEquals(50, count);
		ei.reset();
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		assertEquals(75, count);
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		assertEquals(75, count);
	}

	public void testExecutionInitializerWithStrings() throws BadParameterException {
		final AlgorithmSettingsGridIterator mas = new AlgorithmSettingsGridIterator(TestHelper.getPeriod());
		mas.add(new MpString("n", Arrays.asList(new String[] { "asd" })));
		mas.add(new MpString("d", Arrays.asList(new String[] { "asd", "dfg", "rty" })));
		mas.add(new MpString("o", Arrays.asList(new String[] { "hello", "world", "my", "dear" })));
		final ExecutionInitializer ei = new ExecutionInitializer("e", "a", mas);
		int count = 0;
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		assertEquals(12, count);
	}

	public void testExecutionInitializerWithEverything() throws BadParameterException {
		final AlgorithmSettingsGridIterator mas = new AlgorithmSettingsGridIterator(TestHelper.getPeriod());
		mas.add(new MpString("n", Arrays.asList(new String[] { "asd" })));
		mas.add(new MpString("d", Arrays.asList(new String[] { "asd", "dfg", "rty" })));
		mas.add(new MpString("o", Arrays.asList(new String[] { "hello", "world", "my", "dear" })));
		mas.add(new MpSubExecution("execution", Arrays.asList(new String[] { "name1", "name2", "name3", "name4" })));
		final ExecutionInitializer ei = new ExecutionInitializer("e", "a", mas);
		int count = 0;
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		assertEquals(48, count);
	}
}
