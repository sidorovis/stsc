package stsc.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class AlgorithmSettingsTest extends TestCase {

	public void testAlgorithmsSettings() {
		final AlgorithmSettings as = TestHelper.getAlgorithmSettings();
		assertNull(as.get("a"));
		assertNotNull(as.set("a", new Double(14.05)));
		assertNotNull(as.set("b", 14.05));

		assertEquals(Double.valueOf(as.get("b")), Double.valueOf(as.get("a")));

		final AlgorithmSetting<Double> asd = new AlgorithmSetting<Double>(0.0);
		asd.setValue(Double.valueOf(as.get("a")));
		assertEquals(14.05, asd.getValue());
	}

	public void testReadWriteAlgorithmsSettings() throws IOException {
		final AlgorithmSettings as = TestHelper.getAlgorithmSettings();
		as.addSubExecutionName("first_name").addSubExecutionName("add second Named&9h4t9\n fjiaby \0 world");
		as.set("key", 10.0000);
		as.set("e98h 3h aweiouhj w", "wthrth e hw ");

		ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		as.writeExternal(oos);
		oos.flush();
		// os.flush();

		byte[] buffer = os.toByteArray();
		InputStream is = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(is);
		final AlgorithmSettings asCopy = AlgorithmSettings.read(ois);

		assertEquals(as.getPeriod().getFrom(), asCopy.getPeriod().getFrom());
		assertEquals(as.getPeriod().getTo(), asCopy.getPeriod().getTo());

		assertEquals(asCopy.get("key"), "10.0");
		assertEquals(asCopy.get("e98h 3h aweiouhj w"), "wthrth e hw ");

		assertEquals("first_name", asCopy.getSubExecutions().get(0));
		assertEquals("add second Named&9h4t9\n fjiaby \0 world", asCopy.getSubExecutions().get(1));
	}
}
