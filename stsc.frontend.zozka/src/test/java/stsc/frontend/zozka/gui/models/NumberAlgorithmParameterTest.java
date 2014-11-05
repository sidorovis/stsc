package stsc.frontend.zozka.gui.models;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class NumberAlgorithmParameterTest {

	@Test
	public void testIntegerParPatternCreateAlgorithmController() {
		final Pattern p = NumberAlgorithmParameter.integerParPattern;
		for (int i = -2000; i < 2000; ++i) {
			Assert.assertTrue(p.matcher(String.valueOf(i)).matches());
		}
		Assert.assertTrue(p.matcher("123187659365").matches());
		Assert.assertTrue(p.matcher("-45643563461").matches());
		Assert.assertFalse(p.matcher("0.2").matches());
		Assert.assertFalse(p.matcher("3345-").matches());
		Assert.assertFalse(p.matcher("").matches());
	}

	public void testDoubleParPatternCreateAlgorithmController() {
		final Pattern p = NumberAlgorithmParameter.doubleParPattern;
		final DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
		for (double i = -200.0; i < 200.0; i += 0.01) {
			Assert.assertTrue(p.matcher(decimalFormat.format(i)).matches());
		}
		Assert.assertTrue(p.matcher("123187659365").matches());
		Assert.assertTrue(p.matcher("-45643563.461").matches());
		Assert.assertTrue(p.matcher("0.2").matches());
		Assert.assertFalse(p.matcher("3345-").matches());
		Assert.assertFalse(p.matcher("45.546.12").matches());
		Assert.assertFalse(p.matcher("").matches());
	}
}
