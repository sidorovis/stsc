package stsc.trading;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import junit.framework.TestCase;

public class MarketSimulatorTest extends TestCase {
	public void testMarketSimulator() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			ParseException, IOException, InterruptedException {
		MarketSimulator marketSimulator = new MarketSimulator();
		marketSimulator.simulate();
	}
}
