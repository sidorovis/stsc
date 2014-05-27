package stsc.algorithms;

import stsc.common.Side;

public class EodPosition {
	final String stockName;
	final Side side;
	int sharesAmount;

	public int getSharedAmount() {
		return sharesAmount;
	}

	public void setSharedAmount(int sharedAmount) {
		this.sharesAmount = sharedAmount;
	}

	public String getStockName() {
		return stockName;
	}

	public Side getSide() {
		return side;
	}

	public EodPosition(String stockName, Side side, int sharesAmount) {
		this.stockName = stockName;
		this.side = side;
		this.sharesAmount = sharesAmount;
	}

	@Override
	public String toString() {
		return stockName + "(" + side.name() + ":" + String.valueOf(sharesAmount) + ")";
	}
}
