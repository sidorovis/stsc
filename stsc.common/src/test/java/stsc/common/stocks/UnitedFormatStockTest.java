package stsc.common.stocks;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import stsc.common.Settings;

public class UnitedFormatStockTest {
	@Test
	public void testUnitedFormatStock() throws IOException {
		final UnitedFormatStock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		assertEquals(94.26, aapl.getDays().get(aapl.getDays().size() - 1).prices.open, Settings.doubleEpsilon);
	}
}
