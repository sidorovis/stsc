package stsc.yahoo;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import stsc.common.UnitedFormatStock;

class DownloadThreadSettings {

	private final ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	private final String dataFolder;
	private final String dataFilteredFolder;

	public DownloadThreadSettings() {
		this("./data/", "./filtered_data/");
	}

	public static DownloadThreadSettings forDataFolder(String dataFolder) {
		return new DownloadThreadSettings(dataFolder, "./filtered_data/");
	}

	public static DownloadThreadSettings forFilteredDataFolder(String filteredDataFolder) {
		return new DownloadThreadSettings("./data/", filteredDataFolder);
	}

	public DownloadThreadSettings(String dataFolder, String dataFilteredFolder) {
		super();
		this.dataFolder = dataFolder;
		this.dataFilteredFolder = dataFilteredFolder;
	}

	public void addTask(String taskName) {
		taskQueue.add(taskName);
	}

	public String getTask() {
		return taskQueue.poll();
	}

	public int taskQueueSize() {
		return taskQueue.size();
	}

	public UnitedFormatStock getStockFromFileSystem(String stockName) {
		UnitedFormatStock s = null;
		try {
			s = UnitedFormatStock.readFromUniteFormatFile(dataFolder + stockName + ".uf");
		} catch (IOException e) {
		}
		return s;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public String getFilteredDataFolder() {
		return dataFilteredFolder;
	}

	public Queue<String> getTaskQueue() {
		return taskQueue;
	}
}
