package stsc.MarketDataDownloader;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarketDataContext {
	ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	public String dataFolder = "./data/";

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

	public String getDataFolder() {
		return dataFolder;
	}
}
