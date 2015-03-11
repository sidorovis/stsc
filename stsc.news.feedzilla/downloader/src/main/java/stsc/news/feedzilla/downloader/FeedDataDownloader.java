package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.service.statistics.DownloaderLogger;
import stsc.common.service.statistics.StatisticType;

/**
 * {@link FeedDataDownloader} is a class that download feed's from FeedZilla and
 * categories them.
 */
public final class FeedDataDownloader {

	private final static double MIN_MULTIPLIER = 1.0;
	private final static double MAX_MULTIPLIER = 4.0;
	private final static double STEP_MULTIPLIER = 1.5;

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedDataDownloader.class);

	private LocalDateTime dayDownloadFrom;
	private final int amountOfArticlesPerRequest;
	private final int articlesWaitTime;
	private List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadFeedReceiver>());

	private FeedZilla feed = new FeedZilla();
	private volatile boolean stopped = false;

	private double multiplier = MIN_MULTIPLIER;

	public FeedDataDownloader(int amountOfArticlesPerRequest, int articlesWaitTime) {
		this(LocalDateTime.now().minusDays(356 * 20), amountOfArticlesPerRequest, articlesWaitTime);
	}

	FeedDataDownloader(LocalDateTime dayDownloadFrom, int amountOfArticlesPerRequest, int articlesWaitTime) {
		this.dayDownloadFrom = dayDownloadFrom;
		this.amountOfArticlesPerRequest = amountOfArticlesPerRequest;
		this.articlesWaitTime = articlesWaitTime;
	}

	public void setDaysToDownload(LocalDateTime dayDownloadFrom) {
		this.dayDownloadFrom = dayDownloadFrom;
	}

	public void stopDownload() throws InterruptedException {
		stopped = true;
	}

	public void addReceiver(LoadFeedReceiver receiver) {
		receivers.add(receiver);
	}

	public boolean download(DownloaderLogger downloaderLogger) throws InterruptedException {
		boolean result = true;
		int amountOfProcessedArticles = 0;
		feed = new FeedZilla();
		final List<Category> categories = DownloadHelper.getCategories(feed, logger);
		if (categories.isEmpty())
			return false;
		for (Category category : categories) {
			final long beginTime = System.currentTimeMillis();
			CallableArticlesDownload.pause();
			final List<Subcategory> subcategories = DownloadHelper.getSubcategories(feed, category, logger);
			if (subcategories.isEmpty()) {
				result = false;
			}
			for (Subcategory subcategory : subcategories) {
				try {
					amountOfProcessedArticles += getArticles(category, subcategory, dayDownloadFrom, multiplier);
				} catch (Exception e) {
					logger.error("getArticles returns " + e.getClass() + " msg: " + e.getMessage() + " for " + subcategory.getDisplayName());
					result = false;
					if (multiplier < MAX_MULTIPLIER) {
						multiplier *= STEP_MULTIPLIER;
					}
				}
				if (stopped) {
					break;
				}
			}
			if (stopped) {
				break;
			}
			if (result) { // there were no errors
				if (multiplier > MIN_MULTIPLIER) {
					multiplier = 1.0 / STEP_MULTIPLIER;
				}
			}
			final long endTime = System.currentTimeMillis();
			downloaderLogger.log(StatisticType.INFO, "Category " + category.getEnglishName() + " downloaded with "
					+ amountOfProcessedArticles + " articles. For day " + dayDownloadFrom + ". Which took: " + (endTime - beginTime)
					+ " millisec.");
		}
		downloaderLogger.log(StatisticType.INFO, "Received amount of articles: " + amountOfProcessedArticles + " --- for date "
				+ dayDownloadFrom.toString());
		return result;
	}

	int getArticles(final Category category, final Subcategory subcategory, final LocalDateTime startOfDay, final double multiplyer)
			throws Exception {
		final FutureTask<Optional<List<Article>>> futureArticles = new FutureTask<>(new CallableArticlesDownload(feed, category,
				subcategory, amountOfArticlesPerRequest, startOfDay));
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				futureArticles.run();
			}
		});
		thread.start();
		final Optional<List<Article>> articles = futureArticles.get((long) (articlesWaitTime * multiplyer), TimeUnit.SECONDS);
		if (!articles.isPresent() || stopped)
			return 0;
		int articlesCount = 0;
		for (Article article : articles.get()) {
			try {
				for (LoadFeedReceiver receiver : receivers) {
					receiver.newArticle(category, subcategory, article);
					if (stopped)
						return articlesCount;
				}
				articlesCount += 1;
			} catch (Exception e) {
				logger.fatal("Error while passing article to receiver: for hashcode create: " + article.toString(), e);
			}
			if (stopped) {
				return articlesCount;
			}
		}
		return articles.get().size();
	}

	public boolean isStopped() {
		return stopped;
	}
}
