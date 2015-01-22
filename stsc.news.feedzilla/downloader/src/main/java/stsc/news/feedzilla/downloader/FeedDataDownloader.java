package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.joda.time.DateTime;

/**
 * {@link FeedDataDownloader} is a class that download feed's from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedDataDownloader.class);

	private int daysToDownload;
	private final int amountOfArticlesPerRequest;
	private List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadFeedReceiver>());

	private final FeedZilla feed = new FeedZilla();
	private Thread thread;
	private volatile boolean stopped = false;

	FeedDataDownloader(int amountOfArticlesPerRequest) {
		this(356 * 20, amountOfArticlesPerRequest);
	}

	FeedDataDownloader(int daysToDownload, int amountOfArticlesPerRequest) {
		this.daysToDownload = daysToDownload;
		this.amountOfArticlesPerRequest = amountOfArticlesPerRequest;
	}

	public void setDaysToDownload(int daysToDownload) {
		this.daysToDownload = daysToDownload;
	}

	public int getDaysToDownload() {
		return daysToDownload;
	}

	public void stopDownload() throws InterruptedException {
		stopped = true;
		if (thread != null) {
			thread.interrupt();
		}
	}

	public boolean isStopped() {
		return stopped;
	}

	void addReceiver(LoadFeedReceiver receiver) {
		receivers.add(receiver);
	}

	public void download() {
		final DateTime downloadPeriod = createNextDateTimeElement();
		int amountOfProcessedArticles = 0;
		final List<Category> categories = getCategories(feed);
		for (Category category : categories) {
			final long beginTime = System.currentTimeMillis();
			CallableArticlesDownload.pause();
			final List<Subcategory> subcategories = getSubcategories(feed, category);
			for (Subcategory subcategory : subcategories) {
				try {
					CallableArticlesDownload.pause();
					amountOfProcessedArticles += getArticles(category, subcategory, downloadPeriod);
				} catch (TimeoutException e) {
					logger.error("getArticles returns TimeoutException: " + e.getMessage() + "; we trying to restart executor.");
					updateExecutor();
				} catch (Exception e) {
					logger.error("getArticles returns", e);
				}
				if (stopped) {
					break;
				}
			}
			if (stopped) {
				break;
			}
			final long endTime = System.currentTimeMillis();
			logger.debug("Category " + category.getEnglishName() + " downloaded with " + amountOfProcessedArticles + " articles. For day "
					+ downloadPeriod + ". Which took: " + (endTime - beginTime) + " millisec.");
		}
		logger.info("Received amount of articles: " + amountOfProcessedArticles + " --- for date " + downloadPeriod.toString());
	}

	public static List<Category> getCategories(FeedZilla feed) {
		for (int amountOfTries = 0; amountOfTries < CallableArticlesDownload.TRIES_COUNT; ++amountOfTries) {
			try {
				return feed.getCategories();
			} catch (Exception e) {
				logger.error("Downloading categories throw exception: " + e.getMessage());
			}
			CallableArticlesDownload.pause();
		}
		return Collections.emptyList();
	}

	public static List<Subcategory> getSubcategories(FeedZilla feed, Category category) {
		for (int amountOfTries = 0; amountOfTries < CallableArticlesDownload.TRIES_COUNT; ++amountOfTries) {
			try {
				return feed.getSubcategories(category);
			} catch (Exception e) {
				logger.debug("Downloading subcategories throw exception: " + e.getMessage());
			}
			CallableArticlesDownload.pause();
		}
		return Collections.emptyList();
	}

	private DateTime createNextDateTimeElement() {
		return DateTime.now().minusDays(daysToDownload).withTimeAtStartOfDay();
	}

	int getArticles(final Category category, final Subcategory subcategory, final DateTime startOfDay) throws Exception {
		final FutureTask<Optional<List<Article>>> futureArticles = new FutureTask<>(new CallableArticlesDownload(feed, category,
				subcategory, amountOfArticlesPerRequest, startOfDay));
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				futureArticles.run();
			}
		});
		final long beginArticlesDownloadTime = System.currentTimeMillis();
		thread.start();
		final Optional<List<Article>> articles = futureArticles.get(60, TimeUnit.SECONDS);

		final long endArticlesDownloadTime = System.currentTimeMillis();
		logger.debug("Timing for articles download: " + (endArticlesDownloadTime - beginArticlesDownloadTime));

		if (!articles.isPresent() || stopped)
			return 0;
		int articlesCount = 0;
		long timeReceiversDifferenceSum = 0;
		for (Article article : articles.get()) {
			try {
				for (LoadFeedReceiver receiver : receivers) {
					final long beginProcessing = System.currentTimeMillis();
					receiver.newArticle(category, subcategory, article);
					final long endProcessing = System.currentTimeMillis();
					timeReceiversDifferenceSum += endProcessing - beginProcessing;
					articlesCount += 1;
					if (stopped) {
						logger.debug("Timing for processing: " + timeReceiversDifferenceSum);
						return articlesCount;
					}
				}
			} catch (Exception e) {
				logger.fatal("Error while passing article to receiver: for hashcode create: " + article.toString(), e);
			}
			if (stopped) {
				logger.debug("Timing for processing: " + timeReceiversDifferenceSum);
				return articlesCount;
			}
		}
		logger.debug("Timing for processing: " + timeReceiversDifferenceSum);
		return articles.get().size();
	}

	public void updateExecutor() {
		thread.interrupt();
	}

}
