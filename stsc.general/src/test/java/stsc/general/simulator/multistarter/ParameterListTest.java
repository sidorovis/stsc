package stsc.general.simulator.multistarter;

import org.junit.Assert;
import org.junit.Test;

public class ParameterListTest {

	private ParameterList<Double> getList() throws BadParameterException {
		final ParameterList<Double> list = new ParameterList<Double>();
		list.add(new MpDouble("asd", 0.0, 1.0, 0.1));
		list.add(new MpDouble("vrt", 0.5, 1.0, 0.1));
		return list;
	}

	@Test
	public void testParameterList() throws BadParameterException {
		final ParameterList<Double> list = getList();
		list.increment();
		list.increment();
		list.increment();
		list.increment();
		list.increment();
		final ParameterList<Double> clone = list.clone();
		Assert.assertEquals("5: [asd:0.0 from (0.1|0.0:1.0), vrt:0.5 from (0.1|0.5:1.0)]", list.toString());
		Assert.assertEquals("0: [asd:0.0 from (0.1|0.0:1.0), vrt:0.5 from (0.1|0.5:1.0)]", clone.toString());
	}

	@Test
	public void testParameterListSize() throws BadParameterException {
		final ParameterList<Double> list = getList();
		Assert.assertEquals(50L, list.size());
	}
}
