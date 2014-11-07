package stsc.common.collections;

import org.junit.Assert;
import org.junit.Test;

public class SortedByRatingTest {
	@Test
	public void testSortedByRating() {
		final SortedByRating<Integer> s = new SortedByRating<>();
		s.addElement(14.5, 15);
		s.addElement(14.5, 16);
		s.addElement(14.5, 17);
		s.addElement(14.5, 18);
		s.addElement(14.5, 19);
		s.addElement(14.5, 20);
		s.addElement(14.5, 21);
		Assert.assertEquals(7, s.size());
		s.deleteLast();
		Assert.assertEquals(6, s.size());
		Assert.assertEquals(20, s.getValuesAsList().get(5).intValue());
		s.addElement(14.6, 21);
		Assert.assertEquals(7, s.size());
		Assert.assertEquals(21, s.getValuesAsList().get(6).intValue());
		s.deleteLast();
		Assert.assertEquals(6, s.size());
		Assert.assertEquals(21, s.getValuesAsList().get(5).intValue());
		s.deleteLast();
		Assert.assertEquals(21, s.getValuesAsList().get(4).intValue());
		Assert.assertEquals(18, s.getValuesAsList().get(3).intValue());
		for (int i = 0; i < 20; ++i)
			s.deleteLast();
	}
}
