package stsc.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.testhelper.TestAlgorithmsHelper;
import stsc.common.Settings;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.BadAlgorithmException;

public final class AlgorithmSettingsTest {

	@Test
	public void testAlgorithmsSettings() {
		final AlgorithmSettingsImpl as = TestAlgorithmsHelper.getSettings();
		Assert.assertNull(as.getString("a"));
		Assert.assertNotNull(as.setDouble("a", new Double(14.05)));
		Assert.assertNotNull(as.setDouble("b", 14.05));

		Assert.assertEquals(as.getDouble("b"), as.getDouble("a"), Settings.doubleEpsilon);

		final AlgorithmSettingImpl<Double> asd = new AlgorithmSettingImpl<Double>(0.0);
		asd.setValue(as.getDouble("a"));
		Assert.assertEquals(14.05, asd.getValue(), Settings.doubleEpsilon);
	}

	@Test
	public void testReadWriteAlgorithmsSettings() throws IOException {
		final AlgorithmSettingsImpl as = TestAlgorithmsHelper.getSettings();
		as.addSubExecutionName("first_name").addSubExecutionName("add second Named&9h4t9\n fjiaby \0 world");
		as.setDouble("key", 10.0000);
		as.setString("e98h 3h aweiouhj w", "wthrth e hw ");

		ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		as.writeExternal(oos);
		oos.flush();

		byte[] buffer = os.toByteArray();
		InputStream is = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(is);
		final AlgorithmSettings asCopy = AlgorithmSettingsImpl.read(ois);

		Assert.assertEquals(as.getPeriod().getFrom(), asCopy.getPeriod().getFrom());
		Assert.assertEquals(as.getPeriod().getTo(), asCopy.getPeriod().getTo());

		Assert.assertEquals(asCopy.getDouble("key"), 10.0, Settings.doubleEpsilon);
		Assert.assertEquals(asCopy.getString("e98h 3h aweiouhj w"), "wthrth e hw ");

		Assert.assertEquals("first_name", asCopy.getSubExecutions().get(0));
		Assert.assertEquals("add second Named&9h4t9\n fjiaby \0 world", asCopy.getSubExecutions().get(1));
	}

	@Test
	public void testGetIntegerDoubleTypes() throws BadAlgorithmException {
		final AlgorithmSettingsImpl as = new AlgorithmSettingsImpl(TestAlgorithmsHelper.getPeriod());
		as.setInteger("asd", Integer.valueOf(15));
		as.setInteger("4asd", Integer.valueOf(1231));
		as.setDouble("param", Double.valueOf(1231.0));
		as.setDouble("para3m", Double.valueOf(125.454));
		Assert.assertEquals(Integer.valueOf(15), as.getInteger("asd"));
		Assert.assertEquals(Integer.valueOf(1231), as.getInteger("4asd"));
		Assert.assertEquals(Double.valueOf(1231.0), as.getDouble("param"));
		Assert.assertEquals(Double.valueOf(125.454), as.getDouble("para3m"));

		as.setDouble("kill", 15.343);
		final AlgorithmSetting<Double> d = as.getDoubleSetting("kill", 0.0);
		Assert.assertEquals(15.343, d.getValue(), Settings.doubleEpsilon);
	}
}
