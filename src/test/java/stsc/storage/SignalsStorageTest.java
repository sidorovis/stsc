package stsc.storage;

import java.sql.Date;

import junit.framework.TestCase;

public class SignalsStorageTest extends TestCase {

	class TestSignal extends ExecutionSignal {
		public final int id;

		public TestSignal(int id) {
			this.id = id;
		}
	}

	public void testSignalsStorage() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorage();
		signalsStorage.registerSignalsFromExecution("e1", TestSignal.class);
		signalsStorage.addSignal("e1", Date.valueOf("2010-10-20"), new TestSignal(12));
		TestSignal ts = (TestSignal)signalsStorage.getSignal("e1", Date.valueOf("2010-10-20"));
		assertEquals(12, ts.id);
	}
}
