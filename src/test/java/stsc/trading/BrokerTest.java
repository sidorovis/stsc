package stsc.trading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

public class BrokerTest extends TestCase {
	public void testBroker() throws IOException {
		Broker broker = new Broker();
		broker.setToday(new Date());
		assertEquals(1000, broker.buy("aapl", Side.LONG, 1000));
		assertEquals(2000, broker.sell("aapl", Side.SHORT, 2000));

		try (FileWriter fw = new FileWriter("./test/out_file.txt")) {
			broker.getTradingLog().printOut(fw);
		}

		File out = new File("./test/out_file.txt");
		assertEquals(57, out.length());
		out.delete();
	}

	public void testBrokerTradingCalculating() {
		Broker broker = new Broker();
		
	}
}
