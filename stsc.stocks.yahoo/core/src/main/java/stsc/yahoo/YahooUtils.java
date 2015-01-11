package stsc.yahoo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import stsc.common.stocks.UnitedFormatStock;

public class YahooUtils {

	private YahooUtils() {
	}

	public static void copyFilteredStockFile(String dataFolder, String filteredDataFolder, String stockName) throws IOException {
		final File originalFile = new File(UnitedFormatStock.generatePath(dataFolder, stockName));
		final File filteredFile = new File(UnitedFormatStock.generatePath(filteredDataFolder, stockName));
		if (filteredFile.exists() && originalFile.exists() && filteredFile.length() == originalFile.length()) {
			// filteter file exists and have the same size, so do nothing
		} else
			Files.copy(originalFile.toPath(), filteredFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static YahooSettings createSettings() throws IOException {
		return new YahooSettings("./data/", "./filtered_data/");
	}

	public static YahooSettings createSettings(String dataFolder, String filteredDataFolder) throws IOException {
		return new YahooSettings(dataFolder, filteredDataFolder);
	}
}
