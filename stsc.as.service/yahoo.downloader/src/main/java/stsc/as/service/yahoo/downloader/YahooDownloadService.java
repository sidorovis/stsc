package stsc.as.service.yahoo.downloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.StopableApp;
import stsc.common.service.YahooDownloaderSettings;
import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.YahooDownloaderDatabaseSettings;
import stsc.database.service.schemas.OrmliteYahooDownloaderLogger;
import stsc.database.service.storages.YahooDownloaderDatabaseStorage;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;
import stsc.yahoo.downloader.YahooDownloadCourutine;

final class YahooDownloadService implements StopableApp {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private final Logger logger = LogManager.getLogger(YahooDownloadService.class.getName());

	private final String settingName = "yahoo_downloader";

	private volatile boolean stopped = false;
	private YahooDownloaderSettings settings;
	private final YahooDownloaderDatabaseStorage settingsStorage;
	private final OrmliteYahooDownloaderLogger downloaderLogger;

	private Optional<YahooDownloadCourutine> courutine = Optional.empty();
	private Object lock = new Object();

	private YahooDownloadService() throws FileNotFoundException, IOException, SQLException {
		final YahooDownloaderDatabaseSettings databaseSettings = new YahooDownloaderDatabaseSettings("./config/yahoo_downloader_production.properties");
		this.settingsStorage = new YahooDownloaderDatabaseStorage(databaseSettings);
		this.downloaderLogger = new OrmliteYahooDownloaderLogger(logger, settingsStorage, settingName, getProcessId(), getStartTime());
		this.settings = settingsStorage.getSettings(settingName);
		downloaderLogger.log(StatisticType.TRACE, "YahooDownloadService initialized");
	}

	private void startExecutionCycle() throws InterruptedException, SQLException {
		long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		while (!stopped) {
			readSettings();
			downloaderLogger.log(StatisticType.TRACE, "Going to download yahoo datafeed");
			download();
			downloaderLogger.log(StatisticType.TRACE, "Downloading finished");
			if (stopped) {
				break;
			}
			final long timeDiff = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - start;
			final int intervalBetweenExecutionsSeconds = settings.intervalBetweenExecutions();
			if (timeDiff < intervalBetweenExecutionsSeconds) {
				synchronized (lock) {
					final long secondsSleepInterval = (intervalBetweenExecutionsSeconds - timeDiff);
					final double minutesSleepInterval = (double) secondsSleepInterval / 3600;
					downloaderLogger.log(StatisticType.TRACE, "Sleep until next cycle: " + (intervalBetweenExecutionsSeconds - timeDiff)
							+ " seconds (" + minutesSleepInterval + " hours)");
					lock.wait(1000 * (intervalBetweenExecutionsSeconds - timeDiff));
				}
			}
			start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		}
	}

	private void download() throws SQLException {
		try {
			final YahooDownloaderSettings s = settings;
			final boolean downloadExisted = s.downloadOnlyExisted();
			final YahooSettings settings = YahooUtils.createSettings();
			final boolean downloadByPattern = s.downloadByPattern();
			final String startPattern = s.patternNameFrom();
			final String endPattern = s.patternNameTo();
			final int stockNameMinLength = s.stockNameFrom();
			final int stockNameMaxLength = s.stockNameTo();
			final int downloadThreadSize = s.threadAmount();
			final YahooDownloadCourutine courutine = new YahooDownloadCourutine(downloaderLogger, downloadExisted, settings,
					downloadByPattern, startPattern, endPattern, stockNameMinLength, stockNameMaxLength, downloadThreadSize);
			synchronized (this) {
				this.courutine = Optional.of(courutine);
			}
			courutine.start();
			synchronized (this) {
				this.courutine = Optional.empty();
			}
		} catch (Exception e) {
			downloaderLogger.log(StatisticType.ERROR, "download() " + e.getMessage());
		}
	}

	private Integer getProcessId() {
		final String name = ManagementFactory.getRuntimeMXBean().getName();
		final String id = name.substring(0, name.indexOf('@'));
		return Integer.valueOf(id);
	}

	private Date getStartTime() {
		final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		return new Date(startTime);
	}

	private YahooDownloaderSettings readSettings() throws SQLException {
		try {
			settings = settingsStorage.getSettings(settingName);
		} catch (SQLException e) {
			logger.fatal("readSettings() " + e.getMessage());
		}
		return settings;
	}

	@Override
	public void start() throws Exception {
		startExecutionCycle();
	}

	@Override
	public void stop() throws Exception {
		this.stopped = true;
		synchronized (this) {
			if (courutine.isPresent()) {
				courutine.get().stop();
			}
		}
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void log(Level logLevel, String message) {
		logger.error("log: " + logLevel.getName() + ", message:" + message);
	}

	public static void main(String[] args) {
		try {
			final StopableApp app = new YahooDownloadService();
			ApplicationHelper.createHelper(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
