package stsc.yahoo;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testNextPermutation() {
		Assert.assertEquals("b", StringUtils.nextPermutation("a"));
		Assert.assertEquals("e", StringUtils.nextPermutation("d"));
		Assert.assertEquals("z", StringUtils.nextPermutation("y"));

		Assert.assertEquals("aa", StringUtils.nextPermutation("z"));
		Assert.assertEquals("ab", StringUtils.nextPermutation("aa"));
		Assert.assertEquals("at", StringUtils.nextPermutation("as"));

		Assert.assertEquals("ba", StringUtils.nextPermutation("az"));
		Assert.assertEquals("be", StringUtils.nextPermutation("bd"));

		Assert.assertEquals("ca", StringUtils.nextPermutation("bz"));
		Assert.assertEquals("da", StringUtils.nextPermutation("cz"));

		Assert.assertEquals("aaa", StringUtils.nextPermutation("zz"));
		Assert.assertEquals("aad", StringUtils.nextPermutation("aac"));

		Assert.assertEquals("aaaa", StringUtils.nextPermutation("zzz"));
	}

	@Test
	public void testComparePatterns() {
		Assert.assertEquals(true, StringUtils.comparePatterns("a", "b") < 0);
		Assert.assertEquals(true, StringUtils.comparePatterns("b", "a") > 0);

		Assert.assertEquals(true, StringUtils.comparePatterns("aaa", "zz") > 0);

		Assert.assertEquals(true, StringUtils.comparePatterns("ab", "aa") > 0);

		Assert.assertEquals(0, StringUtils.comparePatterns("aa", "aa"));
	}
}
