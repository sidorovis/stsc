package stsc.as.service.yahoo.downloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.YahooDownloaderSettings;
import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.DatabaseSettings;
import stsc.database.service.settings.DatabaseSettingsStorage;
import stsc.database.service.statistics.OrmliteYahooDownloaderStatistics;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;
import stsc.yahoo.downloader.YahooDownloadCourutine;

public class YahooDownloadService implements ApplicationHelper.StopableApp {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private final int INTERVAL_BETWEEN_EXECUTIONS = 60 * 60;

	private final Logger logger = LogManager.getLogger(YahooDownloadService.class.getName());

	private final String settingName = "yahoo_downloader";

	private final int processId;
	private final Date startDateTime;
	private volatile boolean stopped = false;
	private final DatabaseSettingsStorage settingsStorage;
	private YahooDownloaderSettings defaultYahooDownloaderSettings;

	private Queue<OrmliteYahooDownloaderStatistics> statisticsQueue = new SynchronousQueue<OrmliteYahooDownloaderStatistics>();

	private Optional<YahooDownloadCourutine> courutine = Optional.empty();
	private Object lock = new Object();

	private YahooDownloadService() throws FileNotFoundException, IOException, SQLException {
		this.processId = getId();
		this.startDateTime = getStartTime();
		final DatabaseSettings databaseSettings = new DatabaseSettings("./config/feedzilla_production.properties");
		this.settingsStorage = new DatabaseSettingsStorage(databaseSettings);
		this.defaultYahooDownloaderSettings = settingsStorage.getYahooDatafeedSettings(settingName);
		logMessage(StatisticType.TRACE, "Yahoo Download Service Started");
		storeStatisticsQueue();
	}

	private void startExecutionCycle() throws InterruptedException {
		long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		while (!stopped) {
			readSettings();
			logMessage(StatisticType.TRACE, "going to download");
			download();
			logMessage(StatisticType.TRACE, "downloading finished");
			storeStatisticsQueue();
			if (stopped) {
				break;
			}
			final long timeDiff = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - start;
			if (timeDiff < INTERVAL_BETWEEN_EXECUTIONS) {
				synchronized (lock) {
					lock.wait(1000 * (INTERVAL_BETWEEN_EXECUTIONS - timeDiff));
				}
			}
			start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		}
		logStatisticsQueue();
	}

	private void download() {
		try {
			final YahooDownloaderSettings s = defaultYahooDownloaderSettings;
			final boolean downloadExisted = s.downloadOnlyExisted();
			final YahooSettings settings = YahooUtils.createSettings();
			final boolean downloadByPattern = s.downloadByPattern();
			final String startPattern = s.patternNameFrom();
			final String endPattern = s.patternNameTo();
			final int stockNameMinLength = s.stockNameFrom();
			final int stockNameMaxLength = s.stockNameTo();
			final int downloadThreadSize = s.threadAmount();
			final YahooDownloadCourutine courutine = new YahooDownloadCourutine(logger, downloadExisted, settings, downloadByPattern,
					startPattern, endPattern, stockNameMinLength, stockNameMaxLength, downloadThreadSize);
			synchronized (this) {
				this.courutine = Optional.of(courutine);
			}
			courutine.start();
			synchronized (this) {
				this.courutine = Optional.empty();
			}
		} catch (Exception e) {
			logException(e);
		}
		logMessage(StatisticType.TRACE, "Download cycle finished");
	}

	private Integer getId() {
		final String name = ManagementFactory.getRuntimeMXBean().getName();
		final String id = name.substring(0, name.indexOf('@'));
		return Integer.valueOf(id);
	}

	private Date getStartTime() {
		final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		return new Date(startTime);
	}

	private YahooDownloaderSettings readSettings() {
		try {
			defaultYahooDownloaderSettings = settingsStorage.getYahooDatafeedSettings(settingName);
		} catch (SQLException e) {
			logException(e);
		}
		return defaultYahooDownloaderSettings;
	}

	private void storeStatisticsQueue() {
		while (!statisticsQueue.isEmpty()) {
			final OrmliteYahooDownloaderStatistics v = statisticsQueue.peek();
			try {
				settingsStorage.setYahooDatafeedStatistics(v);
				statisticsQueue.poll();
			} catch (SQLException e) {
				logException(e);
				break;
			}
		}
	}

	private void logStatisticsQueue() {
		while (!statisticsQueue.isEmpty()) {
			final OrmliteYahooDownloaderStatistics v = statisticsQueue.peek();
			logger.error("Message that was not stored to database: " + v.toString());
			statisticsQueue.poll();
		}
	}

	private void logMessage(StatisticType type, String message) {
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(type);
		v.setMessage(message);
		statisticsQueue.offer(v);
	}

	private void logException(Exception e) {
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(StatisticType.ERROR);
		v.setMessage(e.getMessage());
		statisticsQueue.offer(v);
	}

	private OrmliteYahooDownloaderStatistics createStatistics() {
		final OrmliteYahooDownloaderStatistics v = new OrmliteYahooDownloaderStatistics(settingName);
		v.setProcessId(processId);
		v.setStartDate(startDateTime);
		return v;
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
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(StatisticType.ERROR);
		v.setMessage("log: " + logLevel.getName() + ", message:" + message);
		statisticsQueue.offer(v);
	}

	public static void main(String[] args) {
		try {
			final ApplicationHelper.StopableApp app = new YahooDownloadService();
			ApplicationHelper.createHelper(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
