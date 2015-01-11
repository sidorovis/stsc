package stsc.yahoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stsc.common.stocks.Stock;

class StockReadThread implements Runnable {

	private final YahooSettings settings;
	private final List<LoadStockReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadStockReceiver>());

	public StockReadThread(YahooSettings settings) {
		this.settings = settings;
	}

	public void addReceiver(final LoadStockReceiver receiver) {
		receivers.add(receiver);
	}

	public void addReceivers(List<LoadStockReceiver> receiversToAdd) {
		receivers.addAll(receiversToAdd);
	}

	@Override
	public void run() {
		String task = settings.getTask();
		while (task != null) {
			Stock s = settings.getStockFromFileSystem(task);
			if (s != null) {
				updateReceivers(s);
			}
			task = settings.getTask();
		}
	}

	private void updateReceivers(Stock s) {
		for (LoadStockReceiver receiver : receivers) {
			receiver.newStock(s);
		}
	}

}