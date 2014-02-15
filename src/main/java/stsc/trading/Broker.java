package stsc.trading;

public class Broker {

	public Broker() {

	}

	// algorithms interface
	/**
	 * @return bought amount of actions
	 */
	public int buy(String stockName, Side side, int moneyAmount) {
		return 0;
	}
	/**
	 * @return sold amount of actions
	 */
	public int sell(String stockName, Side side, int sharedAmount) {
		return 0;
	}
}
