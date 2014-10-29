package stsc.yahoo;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import stsc.common.stocks.UnitedFormatStock;

public class YahooSettings {

	private final ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	private String dataFolder = "./data/";
	private String filteredDataFolder = "./filtered_data/";

	YahooSettings(String dataFolder, String filteredDataFolder) throws IOException {
		this.dataFolder = checkFolder(dataFolder, "Bad data folder");
		this.filteredDataFolder = checkFolder(filteredDataFolder, "Bad filtered data folder");
	}

	private String checkFolder(final String dataFolder, final String message) throws IOException {
		final File dataFolderFile = new File(dataFolder);
		if (dataFolderFile.exists() && dataFolderFile.isDirectory()) {
			return dataFolderFile.getPath() + File.separatorChar;
		} else {
			throw new IOException(message + ": " + this.dataFolder);
		}
	}

	public int taskQueueSize() {
		return taskQueue.size();
	}

	public YahooSettings addTask(String s) {
		taskQueue.add(s);
		return this;
	}

	public String getTask() {
		return taskQueue.poll();
	}

	public String generateUniteFormatPath(String stockName) {
		return UnitedFormatStock.generatePath(dataFolder, stockName);
	}

	public UnitedFormatStock getStockFromFileSystem(String stockName) {
		UnitedFormatStock s = null;
		try {
			s = UnitedFormatStock.readFromUniteFormatFile(generateUniteFormatPath(stockName));
		} catch (Exception e) {
		}
		return s;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public String getFilteredDataFolder() {
		return filteredDataFolder;
	}

	public Queue<String> getTaskQueue() {
		return taskQueue;
	}

}
