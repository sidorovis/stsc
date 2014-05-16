package stsc.yahoo;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import stsc.common.UnitedFormatStock;

public class YahooFilesystemDatafeedSettings {

	ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<String>();
	private String dataFolder = "./data/";
	private String filteredDataFolder = "./filtered_data/";

	YahooFilesystemDatafeedSettings() {
	}

	YahooFilesystemDatafeedSettings(String dataFolder, String filteredDataFolder) {
		this.dataFolder = dataFolder;
		this.filteredDataFolder = filteredDataFolder;
	}

	public int taskQueueSize() {
		return taskQueue.size();
	}

	public YahooFilesystemDatafeedSettings addTask(String s) {
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
		} catch (IOException e) {
		}
		return s;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public String getFilteredDataFolder() {
		return filteredDataFolder;
	}

}
