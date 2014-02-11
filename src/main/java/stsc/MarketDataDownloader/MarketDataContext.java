package stsc.MarketDataDownloader;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarketDataContext {
	ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	public String dataFolder = "./data/";
	public String filteredDataFolder = "./filtered_data/";

	public MarketDataContext() throws IOException {
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
	public String generateFilteredBinaryFilePath(String stockName) {
		return filteredDataFolder + stockName + ".bin";
	}

	public String generateBinaryFilePath(String stockName) {
		return dataFolder + stockName + ".bin";
	}
	
	public Stock getStockFromFileSystem(String stockName) {
		Stock s = null;
		try {
			s = Stock.readFromBinFile(generateBinaryFilePath(stockName));
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
		return s;
	}

	
}
