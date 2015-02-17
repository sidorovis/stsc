package stsc.yahoo.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;

/**
 * Download Market Data from Yahoo API.
 * 
 */
public final class MarketDataDownloader implements ApplicationHelper.StopableApp {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	private final YahooSettings settings = YahooUtils.createSettings();
	private int downloadThreadSize = 8;
	private int stockNameMinLength = 5;
	private int stockNameMaxLength = 5;
	private boolean downloadExisted = false;
	private boolean downloadByPattern = false;
	private String startPattern = "a";
	private String endPattern = "zz";

	private final YahooDownloadCourutine downloadCourutine;

	private void readProperties() throws IOException {
		FileInputStream in = new FileInputStream("config/yahoo_fetcher.ini");

		Properties p = new Properties();
		p.load(in);
		in.close();

		downloadThreadSize = Integer.parseInt(p.getProperty("thread.amount"));
		downloadExisted = Boolean.parseBoolean(p.getProperty("download_existed"));
		if (!downloadExisted) {
			downloadByPattern = Boolean.parseBoolean(p.getProperty("download_by_pattern"));
			if (downloadByPattern) {
				startPattern = p.getProperty("pattern.start");
				endPattern = p.getProperty("pattern.end");
			} else {
				stockNameMinLength = Integer.parseInt(p.getProperty("stock_name_min.size"));
				stockNameMaxLength = Integer.parseInt(p.getProperty("stock_name_max.size"));
			}
		}
	}

	MarketDataDownloader() throws InterruptedException, IOException {
		readProperties();
		downloadCourutine = new YahooDownloadCourutine(logger, downloadExisted, settings, downloadByPattern, startPattern, endPattern,
				stockNameMinLength, stockNameMaxLength, downloadThreadSize);
	}

	public static void main(String[] args) {
		try {
			final MarketDataDownloader downloader = new MarketDataDownloader();
			ApplicationHelper.createHelper(downloader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() throws Exception {
		downloadCourutine.start();
	}

	@Override
	public void stop() throws Exception {
		downloadCourutine.stop();
	}

	@Override
	public void log(Level logLevel, String message) {
		logger.warn("log: " + logLevel.getName() + ", message: " + message);
	}
}
