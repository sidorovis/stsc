package stsc.storage;

import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.BadSignalException;
import stsc.common.signals.Signal;
import stsc.common.signals.SerieSignal;
import stsc.common.storage.SignalsStorage;
import stsc.signals.series.CommonSignalsSerie;

public class SignalsStorageTest {

	static public class TestEodSignal extends SerieSignal {
		public final int id;

		public TestEodSignal(int id) {
			this.id = id;
		}
	}

	@Test
	public void testSignalsStorage() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorageImpl();
		signalsStorage.registerEodAlgorithmSerie("e1", new CommonSignalsSerie<SerieSignal>(TestEodSignal.class));
		final Date d = new LocalDate(2010, 10, 20).toDate();
		signalsStorage.addEodSignal("e1", d, new TestEodSignal(12));
		final Signal<? extends SerieSignal> ts = signalsStorage.getEodSignal("e1", d);
		Assert.assertEquals(12, ts.getSignal(TestEodSignal.class).id);
		Assert.assertEquals(0, ts.getIndex());
		Assert.assertEquals(d, ts.getDate());
	}

	@Test
	public void testSignalsStorageGetByIndex() throws BadSignalException {
		SignalsStorage signalsStorage = new SignalsStorageImpl();
		signalsStorage.registerEodAlgorithmSerie("e1", new CommonSignalsSerie<SerieSignal>(TestEodSignal.class));

		final LocalDate d = new LocalDate(2010, 10, 20);
		signalsStorage.addEodSignal("e1", d.toDate(), new TestEodSignal(12));
		signalsStorage.addEodSignal("e1", d.plusDays(1).toDate(), new TestEodSignal(15));
		signalsStorage.addEodSignal("e1", d.plusDays(2).toDate(), new TestEodSignal(13));
		signalsStorage.addEodSignal("e1", d.plusDays(3).toDate(), new TestEodSignal(14));

		boolean exception = false;
		try {
			signalsStorage.addEodSignal("e2", d.plusDays(100).toDate(), new TestEodSignal(100));
		} catch (BadSignalException e) {
			exception = true;
		}
		Assert.assertTrue(exception);

		Assert.assertEquals(12, signalsStorage.getEodSignal("e1", 0).getSignal(TestEodSignal.class).id);
		Assert.assertEquals(13, signalsStorage.getEodSignal("e1", 2).getSignal(TestEodSignal.class).id);
		Assert.assertEquals(15, signalsStorage.getEodSignal("e1", 1).getSignal(TestEodSignal.class).id);
		Assert.assertEquals(14, signalsStorage.getEodSignal("e1", 3).getSignal(TestEodSignal.class).id);
	}
}
