package stsc.common;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarketDataContext {
	ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	public String dataFolder = "./data/";
	public String filteredDataFolder = "./filtered_data/";

	public MarketDataContext() {
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

	public String generateFilePath(String stockName) {
		return dataFolder + stockName + ".csv";
	}

	public String generateFilteredUniteFormatPath(String stockName) {
		return filteredDataFolder + stockName + ".uf";
	}

	public String generateUniteFormatPath(String stockName) {
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
