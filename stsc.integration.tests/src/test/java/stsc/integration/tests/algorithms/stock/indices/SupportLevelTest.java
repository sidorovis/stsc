package stsc.integration.tests.algorithms.stock.indices;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;

import com.google.common.collect.TreeMultiset;

public class SupportLevelTest {

	@Test
	public void testSortedTreeSet() {
		final TreeMultiset<Double> lastValuesByMax = TreeMultiset.create(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		Assert.assertEquals(4, lastValuesByMax.size());
		Assert.assertEquals(15.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.add(Double.valueOf(9.0));
		lastValuesByMax.add(Double.valueOf(21.0));
		Assert.assertEquals(9.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement());
		Assert.assertEquals(15.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement(), 4);
		Assert.assertEquals(21.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement());
		Assert.assertEquals(0, lastValuesByMax.size());
	}
}
