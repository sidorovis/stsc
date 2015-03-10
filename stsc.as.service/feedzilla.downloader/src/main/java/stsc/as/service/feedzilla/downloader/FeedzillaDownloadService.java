package stsc.as.service.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.FeedzillaDownloaderSettings;
import stsc.common.service.StopableApp;
import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.FeedzillaDownloaderDatabaseSettings;
import stsc.database.service.schemas.OrmliteFeedzillaDownloaderLogger;
import stsc.database.service.storages.FeedzillaDownloaderDatabaseStorage;
import stsc.news.feedzilla.FeedzillaHashStorage;
import stsc.news.feedzilla.downloader.DownloadHelper;
import stsc.news.feedzilla.downloader.FeedDataDownloader;
import stsc.news.feedzilla.downloader.LoadFeedReceiver;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

final class FeedzillaDownloadService implements StopableApp, LoadFeedReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private final Logger logger = LogManager.getLogger(FeedzillaDownloadService.class.getName());

	private final String settingName = "feedzilla_downloader";

	private volatile boolean stopped = false;
	private FeedzillaDownloaderSettings settings;
	private final FeedzillaDownloaderDatabaseStorage settingsStorage;
	private final OrmliteFeedzillaDownloaderLogger downloaderLogger;

	private final FeedDataDownloader downloader;
	private final FeedzillaHashStorage hashStorage;

	private Object lock = new Object();

	public FeedzillaDownloadService() throws IOException, SQLException {
		final FeedzillaDownloaderDatabaseSettings databaseSettings = new FeedzillaDownloaderDatabaseSettings(
				"./config/feedzilla_downloader_production.properties");
		this.settingsStorage = new FeedzillaDownloaderDatabaseStorage(databaseSettings);
		this.downloaderLogger = new OrmliteFeedzillaDownloaderLogger(logger, settingsStorage, settingName, getProcessId(), getStartTime());
		this.settings = settingsStorage.getSettings(settingName);

		this.downloader = new FeedDataDownloader(100, settings.articlesWaitTime());
		this.hashStorage = new FeedzillaHashStorage(settings.feedFolder());
		downloader.addReceiver(this);
		hashStorage.readFeedData(DownloadHelper.createDateTimeElement(settings.daysBackDownloadFrom()));

		downloaderLogger.log(StatisticType.TRACE, "YahooDownloadService initialized");

	}

	private FeedzillaDownloaderSettings readSettings() throws SQLException {
		try {
			settings = settingsStorage.getSettings(settingName);
		} catch (SQLException e) {
			logger.fatal("readSettings() " + e.getMessage());
		}
		return settings;
	}

	@Override
	public void start() throws Exception {
		long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		boolean firstDownload = true;
		LocalDateTime lastDownloadDate = DownloadHelper.createDateTimeElement(settings.daysBackDownloadFrom());
		while (!stopped) {
			readSettings();
			downloaderLogger.log(StatisticType.TRACE, "Going to start next download cycle of " + FeedzillaDownloadService.class);

			final LocalDateTime now = DownloadHelper.createDateTimeElement(settings.daysBackDownloadFrom());
			if (download(lastDownloadDate)) {
				lastDownloadDate = now;
			}
			if (firstDownload) {
				firstDownload = true;
				hashStorage.freeArticles();
			}

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

	private boolean download(LocalDateTime lastDownloadDate) throws SQLException, InterruptedException, FileNotFoundException, IOException {
		downloader.setDaysToDownload(lastDownloadDate);
		final boolean result = downloader.download();
		hashStorage.save(lastDownloadDate);
		return result;
	}

	@Override
	public void stop() throws Exception {
		this.stopped = true;
		synchronized (this) {
			downloader.stopDownload();
		}
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void log(Level logLevel, String message) {
		logger.error("log(Level, String): " + logLevel.getName() + " " + message);
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

	@Override
	public void newArticle(Category newCategory, Subcategory newSubcategory, Article newArticle) {
		final FeedzillaFileCategory category = createFeedzillaCategory(newCategory);
		final FeedzillaFileSubcategory subcategory = createFeedzillaSubcategory(category, newSubcategory);
		createFeedzillaArticle(subcategory, newArticle);
	}

	private FeedzillaFileCategory createFeedzillaCategory(Category from) {
		final FeedzillaFileCategory result = new FeedzillaFileCategory(0, from.getDisplayName(), from.getEnglishName(), from.getUrlName());
		return hashStorage.createFeedzillaCategory(result);
	}

	private FeedzillaFileSubcategory createFeedzillaSubcategory(FeedzillaFileCategory category, Subcategory from) {
		final FeedzillaFileSubcategory result = new FeedzillaFileSubcategory(0, category, from.getDisplayName(), from.getEnglishName(),
				from.getUrlName());
		return hashStorage.createFeedzillaSubcategory(category, result);
	}

	private void createFeedzillaArticle(FeedzillaFileSubcategory subcategory, Article from) {
		final FeedzillaFileArticle result = new FeedzillaFileArticle(0, subcategory, from.getAuthor(), from.getPublishDate());
		result.setSource(from.getSource());
		result.setSourceUrl(from.getSourceUrl());
		result.setSummary(from.getSummary());
		result.setTitle(from.getTitle());
		result.setUrl(from.getUrl());
		hashStorage.createFeedzillaArticle(subcategory, result);
	}

	public static void main(String[] args) {
		try {
			final StopableApp app = new FeedzillaDownloadService();
			ApplicationHelper.createHelper(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
