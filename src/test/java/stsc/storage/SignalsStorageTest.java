package stsc.storage;

import java.util.Date;

import org.joda.time.LocalDate;

import stsc.algorithms.EodSignal;
import stsc.storage.SignalsStorage.Handler;
import junit.framework.TestCase;

public class SignalsStorageTest extends TestCase {

	class TestSignal extends EodSignal {
		public final int id;

		public TestSignal(int id) {
			this.id = id;
		}
	}

	public void testSignalsStorage() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorage();
		signalsStorage.registerEodSignalsType("e1", TestSignal.class);
		final Date d = new LocalDate(2010, 10, 20).toDate();
		signalsStorage.addEodSignal("e1", d, new TestSignal(12));
		final Handler<? extends EodSignal> ts = signalsStorage.getEodSignal("e1", new LocalDate(2010, 10, 20).toDate());
		assertEquals(12, ts.getSignal(TestSignal.class).id );
		assertEquals(0, ts.index);
		assertEquals(d, ts.date);
	}
}
