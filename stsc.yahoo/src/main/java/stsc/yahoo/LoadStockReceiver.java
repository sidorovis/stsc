package stsc.yahoo;

import stsc.common.stocks.Stock;

public interface LoadStockReceiver {
	void newStock(Stock newStock);
}