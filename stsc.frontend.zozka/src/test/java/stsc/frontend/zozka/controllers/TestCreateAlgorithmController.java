package stsc.frontend.zozka.controllers;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestCreateAlgorithmController {

	@Test
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

}
