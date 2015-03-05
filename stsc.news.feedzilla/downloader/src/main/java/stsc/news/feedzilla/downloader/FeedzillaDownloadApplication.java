package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.ApplicationHelper;
import stsc.common.service.StopableApp;
import stsc.news.feedzilla.FeedzillaHashStorage;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

final class FeedzillaDownloadApplication implements StopableApp, LoadFeedReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedzillaDownloadApplication.class);
	private static String DEVELOPER_FILENAME = "feedzilla_developer.properties";

	private final String feedFolder;
	private boolean endlessCycle = false;
	private int articlesWaitTime = 20;
	private int daysBackDownloadFrom = 3650;
	private final FeedDataDownloader downloader;
	private final FeedzillaHashStorage hashStorage;

	private Object lock = new Object();

	FeedzillaDownloadApplication() throws SQLException, IOException {
		this(DEVELOPER_FILENAME);
	}

	FeedzillaDownloadApplication(String propertyFile) throws IOException {
		this.feedFolder = readFeedFolderProperty(propertyFile);
		if (feedFolder == null) {
			throw new IOException("There is no setting 'feed.folder' at property file: " + propertyFile);
		}
		this.downloader = new FeedDataDownloader(100, articlesWaitTime);
		this.hashStorage = new FeedzillaHashStorage(feedFolder);
		downloader.addReceiver(this);
		if (endlessCycle) {
			daysBackDownloadFrom = 2;
		}
		hashStorage.readFeedData(DownloadHelper.createDateTimeElement(daysBackDownloadFrom));
	}

	private String readFeedFolderProperty(String propertyFile) throws FileNotFoundException, IOException {
		try (DataInputStream inputStream = new DataInputStream(new FileInputStream("./config/" + propertyFile))) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			this.daysBackDownloadFrom = Integer.valueOf((String) properties.getOrDefault("days.back.download.from", "2"));
			this.endlessCycle = Boolean.valueOf((String) properties.getOrDefault("endless.cycle", "false"));
			this.articlesWaitTime = Integer.valueOf((String) properties.getOrDefault("articles.wait.time", "20"));
			return properties.getProperty("feed.folder");
		}
	}

	@Override
	public void start() throws FileNotFoundException, IOException, InterruptedException {
		if (endlessCycle) {
			startEndless();
		} else {
			startNcycles();
		}
	}

	void startEndless() throws FileNotFoundException, IOException, InterruptedException {
		boolean firstDownload = true;
		LocalDateTime lastDownloadDate = LocalDateTime.now().minusDays(daysBackDownloadFrom).withHour(0).withMinute(0);
		long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		while (!downloader.isStopped()) {
			final LocalDateTime now = LocalDateTime.now().minusDays(daysBackDownloadFrom).withHour(0).withMinute(0);
			if (downloadIteration(lastDownloadDate)) {
				lastDownloadDate = now;
			}
			if (firstDownload) {
				firstDownload = true;
				hashStorage.freeArticles();
			}
			final long timeDiff = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - start;
			final int intervalBetweenExecutionsSeconds = 36000;
			if (timeDiff < intervalBetweenExecutionsSeconds) {
				synchronized (lock) {
					final long secondsSleepInterval = (intervalBetweenExecutionsSeconds - timeDiff);
					final double minutesSleepInterval = (double) secondsSleepInterval / intervalBetweenExecutionsSeconds;
					logger.debug("Sleep until next cycle: " + (intervalBetweenExecutionsSeconds - timeDiff) + " seconds ("
							+ minutesSleepInterval + " hours)");
					lock.wait(1000 * (intervalBetweenExecutionsSeconds - timeDiff));
				}
			}
			start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		}
		logger.info("Stopping now true, we break endless cycle");
	}

	void startNcycles() throws FileNotFoundException, IOException, InterruptedException {
		for (int i = daysBackDownloadFrom; i > 1; --i) {
			if (downloader.isStopped())
				break;
			downloadIteration(DownloadHelper.createDateTimeElement(i));
		}
	}

	private boolean downloadIteration(LocalDateTime downloadFrom) throws FileNotFoundException, IOException, InterruptedException {
		downloader.setDaysToDownload(downloadFrom);
		final boolean result = downloader.download();
		hashStorage.save(downloadFrom);
		return result;
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

	@Override
	public void stop() throws Exception {
		downloader.stopDownload();
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void log(Level logLevel, String message) {
		logger.debug("log: " + logLevel.getName() + ", message: " + message);
	}

	public static void main(String[] args) {
		try {
			final FeedzillaDownloadApplication app = new FeedzillaDownloadApplication(DEVELOPER_FILENAME);
			logger.info("Please enter 'e' and press Enter to stop application.");
			ApplicationHelper.createHelper(app);
		} catch (Exception e) {
			logger.error("Error on main function. ", e);
		}
	}

}
