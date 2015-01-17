package stsc.common.signals;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.BadSignalException;

public class SignalsSerieTest {

	private static class SignalsSerieHelper extends SignalsSerie<Integer> {

		private final Integer dateSignal = 15;
		private Integer indexSignal = 24;

		public SignalsSerieHelper(Class<Integer> signalClass) {
			super(signalClass);
		}

		@Override
		public SignalContainer<Integer> getSignal(Date date) {
			return new SignalContainer<Integer>(0, date, dateSignal);
		}

		@Override
		public SignalContainer<Integer> getSignal(int index) {
			return new SignalContainer<Integer>(index, new Date(), indexSignal);
		}

		@Override
		public void addSignal(Date date, Integer signal) throws BadSignalException {
			indexSignal = signal;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public String toString() {
			return null;
		}

	}

	@Test
	public void testSignalsSerie() throws BadSignalException {
		final SignalsSerieHelper helper = new SignalsSerieHelper(Integer.class);
		Assert.assertEquals(15, helper.getSignal(new Date()).getContent().intValue());
		Assert.assertEquals(24, helper.getSignal(45).getContent().intValue());
		helper.addSignal(new Date(), new Integer(56));
		Assert.assertEquals(56, helper.getSignal(99).getContent().intValue());
	}
}
