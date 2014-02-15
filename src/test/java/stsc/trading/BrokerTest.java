package stsc.trading;

import junit.framework.TestCase;

public class BrokerTest extends TestCase {
	public void testBroker() {
		Broker broker = new Broker();
		broker.buy("aapl", Side.LONG, 1000);
		broker.sell("aapl", Side.SHORT, 2000);
	}
}
