package stsc.as.service.yahoo.downloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.YahooDownloaderSettings;
import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.DatabaseSettings;
import stsc.database.service.settings.DatabaseSettingsStorage;
import stsc.database.service.statistics.OrmliteYahooDownloaderStatistics;

public class YahooDownloadService implements ApplicationHelper.StopableApp {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private final String settingName = "yahoo_downloader";

	private final int processId;
	private final Date startDateTime;
	private volatile boolean stopped = false;
	private final DatabaseSettingsStorage settingsStorage;
	private YahooDownloaderSettings defaultYahooDownloaderSettings;

	private Queue<OrmliteYahooDownloaderStatistics> statisticsQueue = new LinkedList<>();

	private YahooDownloadService() throws FileNotFoundException, IOException, SQLException {
		this.processId = getId();
		this.startDateTime = getStartTime();
		final DatabaseSettings databaseSettings = new DatabaseSettings("./config/feedzilla_production.properties");
		this.settingsStorage = new DatabaseSettingsStorage(databaseSettings);
		this.defaultYahooDownloaderSettings = settingsStorage.getYahooDatafeedSettings(settingName);
	}

	private void startExecutionCycle() {
		while (!stopped) {
			readSettings();
			download();
			storeStatisticsQueue();
		}
		storeStatisticsQueue();
	}

	private void download() {
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			addException(e);
		}
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(StatisticType.TRACE);
		v.setMessage("Cycle Passed");
		statisticsQueue.add(v);
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
			addException(e);
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
				addException(e);
				break;
			}
		}
	}

	private void addException(Exception e) {
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(StatisticType.ERROR);
		v.setMessage(e.getMessage());
		statisticsQueue.add(v);
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
	}

	@Override
	public void log(Level logLevel, String message) {
		final OrmliteYahooDownloaderStatistics v = createStatistics();
		v.setStatisticType(StatisticType.ERROR);
		v.setMessage("log: " + logLevel.getName() + ", message:" + message);
		statisticsQueue.add(v);
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
