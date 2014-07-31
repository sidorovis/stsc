package stsc.general.simulator.multistarter;

import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.ParameterList;
import junit.framework.TestCase;

public class ParameterListTest extends TestCase {
	public void testParameterList() throws BadParameterException {
		final ParameterList<Double> list = new ParameterList<Double>();
		list.add(new MpDouble("asd", 0.0, 1.0, 0.1));
		list.add(new MpDouble("vrt", 0.5, 1.0, 0.1));
		list.increment();
		list.increment();
		list.increment();
		list.increment();
		list.increment();
		final ParameterList<Double> clone = list.clone();
		assertEquals("5: [asd:0.0 from (0.1|0.0:1.0), vrt:0.5 from (0.1|0.5:1.0)]", list.toString());
		assertEquals("0: [asd:0.0 from (0.1|0.0:1.0), vrt:0.5 from (0.1|0.5:1.0)]", clone.toString());
	}
}
