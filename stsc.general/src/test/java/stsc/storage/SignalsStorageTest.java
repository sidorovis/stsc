package stsc.storage;

import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.BadSignalException;
import stsc.common.signals.EodSignal;
import stsc.common.signals.Signal;
import stsc.common.storage.SignalsStorage;
import stsc.signals.series.CommonSignalsSerie;
import junit.framework.TestCase;

public class SignalsStorageTest extends TestCase {

	static public class TestSignal extends EodSignal {
		public final int id;

		public TestSignal(int id) {
			this.id = id;
		}
	}

	public void testSignalsStorage() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorageImpl();
		signalsStorage.registerEodAlgorithmSerie("e1", new CommonSignalsSerie<EodSignal>(TestSignal.class));
		final Date d = new LocalDate(2010, 10, 20).toDate();
		signalsStorage.addEodSignal("e1", d, new TestSignal(12));
		final Signal<? extends EodSignal> ts = signalsStorage.getEodSignal("e1", d);
		assertEquals(12, ts.getSignal(TestSignal.class).id);
		assertEquals(0, ts.getIndex());
		assertEquals(d, ts.getDate());
	}

	public void testSignalsStorageGetByIndex() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorageImpl();
		signalsStorage.registerEodAlgorithmSerie("e1", new CommonSignalsSerie<EodSignal>(TestSignal.class));

		final LocalDate d = new LocalDate(2010, 10, 20);
		signalsStorage.addEodSignal("e1", d.toDate(), new TestSignal(12));
		signalsStorage.addEodSignal("e1", d.plusDays(1).toDate(), new TestSignal(15));
		signalsStorage.addEodSignal("e1", d.plusDays(2).toDate(), new TestSignal(13));
		signalsStorage.addEodSignal("e1", d.plusDays(3).toDate(), new TestSignal(14));

		boolean exception = false;
		try {
			signalsStorage.addEodSignal("e2", d.plusDays(100).toDate(), new TestSignal(100));
		} catch (BadSignalException e) {
			exception = true;
		}
		assertTrue(exception);

		assertEquals(12, signalsStorage.getEodSignal("e1", 0).getSignal(TestSignal.class).id);
		assertEquals(13, signalsStorage.getEodSignal("e1", 2).getSignal(TestSignal.class).id);
		assertEquals(15, signalsStorage.getEodSignal("e1", 1).getSignal(TestSignal.class).id);
		assertEquals(14, signalsStorage.getEodSignal("e1", 3).getSignal(TestSignal.class).id);
	}
}
