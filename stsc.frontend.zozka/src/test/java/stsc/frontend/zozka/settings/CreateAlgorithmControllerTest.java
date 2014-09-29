package stsc.frontend.zozka.settings;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CreateAlgorithmControllerTest extends TestCase {
	public void testParameterNamePatternCreateAlgorithmController() {
		final Pattern p = CreateAlgorithmController.parameterNamePattern;
		Assert.assertTrue(p.matcher("asd_gre_htr34_her_y5hdg_ge57_gerg").matches());
		Assert.assertTrue(p.matcher("JH38fsUJf3_fhwiub__efw2G34575SFEwegwg_wegE_EGE_EWH").matches());
		Assert.assertFalse(p.matcher("asd!").matches());
		Assert.assertFalse(p.matcher("asd.").matches());
		Assert.assertFalse(p.matcher("asd(").matches());
		Assert.assertFalse(p.matcher("asd%FE").matches());
		Assert.assertFalse(p.matcher("").matches());
	}

	public void testIntegerParPatternCreateAlgorithmController() {
		final Pattern p = CreateAlgorithmController.integerParPattern;
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
		final Pattern p = CreateAlgorithmController.doubleParPattern;
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
