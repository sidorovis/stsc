package stsc.yahoo;

import stsc.yahoo.StringUtils;
import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
	public void testNextPermutation() {
		assertEquals("b", StringUtils.nextPermutation("a"));
		assertEquals("e", StringUtils.nextPermutation("d"));
		assertEquals("z", StringUtils.nextPermutation("y"));

		assertEquals("aa", StringUtils.nextPermutation("z"));
		assertEquals("ab", StringUtils.nextPermutation("aa"));
		assertEquals("at", StringUtils.nextPermutation("as"));

		assertEquals("ba", StringUtils.nextPermutation("az"));
		assertEquals("be", StringUtils.nextPermutation("bd"));

		assertEquals("ca", StringUtils.nextPermutation("bz"));
		assertEquals("da", StringUtils.nextPermutation("cz"));

		assertEquals("aaa", StringUtils.nextPermutation("zz"));
		assertEquals("aad", StringUtils.nextPermutation("aac"));

		assertEquals("aaaa", StringUtils.nextPermutation("zzz"));
	}

	public void testComparePatterns() {
		assertEquals(true, StringUtils.comparePatterns("a", "b") < 0);
		assertEquals(true, StringUtils.comparePatterns("b", "a") > 0);

		assertEquals(true, StringUtils.comparePatterns("aaa", "zz") > 0);

		assertEquals(true, StringUtils.comparePatterns("ab", "aa") > 0);

		assertEquals(0, StringUtils.comparePatterns("aa", "aa"));
	}
}
