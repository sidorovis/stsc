package stsc.MarketDataDownloader;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

public class DownloadedStockFilter {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
				"./log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("DownloadedStockFilter");

	static MarketDataContext marketDataContext;

	private void collectDownloadedStockNames() {

		File folder = new File(marketDataContext.getDataFolder());
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if ( file.isFile() && filename.endsWith( ".bin" ))
				marketDataContext.addTask( filename );
		}
	}

	public DownloadedStockFilter() throws IOException {
		logger.trace("downloaded stock filter started");
		marketDataContext = new MarketDataContext();
		collectDownloadedStockNames();
		logger.trace("collected stock names to start filter process: {}", marketDataContext.taskQueueSize());
		
		logger.trace("downloaded stock filter finished");
	}

	public static void main(String[] args) {
		try {
			new DownloadedStockFilter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
