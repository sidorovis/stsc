package stsc.common;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

class MarketDataContextToDelete {
	ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	public String dataFolder = "./data/";
	public String filteredDataFolder = "./filtered_data/";

	public MarketDataContextToDelete() {
	}

	public int taskQueueSize() {
		return taskQueue.size();
	}

	public void addTask(String s) {
		taskQueue.add(s);
	}

	public String getTask() {
		return taskQueue.poll();
	}

	private String generateUniteFormatPath(String stockName) {
		return dataFolder + stockName + ".uf";
	}

	public UnitedFormatStock getStockFromFileSystem(String stockName) {
		UnitedFormatStock s = null;
		try {
			s = UnitedFormatStock.readFromUniteFormatFile(generateUniteFormatPath(stockName));
		} catch (IOException e) {
		}
		return s;
	}

}
