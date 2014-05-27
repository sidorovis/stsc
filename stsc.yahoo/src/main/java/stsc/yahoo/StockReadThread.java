package stsc.yahoo;

import stsc.stocks.Stock;

class StockReadThread implements Runnable {

	public static interface StockReceiver {
		void newStock(Stock newStock);
	}

	private YahooSettings settings;
	private StockReceiver receiver;

	public StockReadThread(YahooSettings settings, final StockReceiver receiver) {
		this.settings = settings;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		String task = settings.getTask();
		while (task != null) {
			Stock s = settings.getStockFromFileSystem(task);
			if (s != null) {
				receiver.newStock(s);
			}
			task = settings.getTask();
		}
	}
}