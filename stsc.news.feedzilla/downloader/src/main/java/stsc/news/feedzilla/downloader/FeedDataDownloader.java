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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
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

	private DateTime dayDownloadFrom;
	private final int amountOfArticlesPerRequest;
	private List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadFeedReceiver>());

	private final FeedZilla feed = new FeedZilla();

	private BlockingQueue<Runnable> tasks = new SynchronousQueue<>();
	private Thread thread;
	private volatile boolean stopped = false;

	FeedDataDownloader(int amountOfArticlesPerRequest) {
		this(new DateTime().minusDays(356 * 20), amountOfArticlesPerRequest);
	}

	FeedDataDownloader(DateTime dayDownloadFrom, int amountOfArticlesPerRequest) {
		this.dayDownloadFrom = dayDownloadFrom;
		this.amountOfArticlesPerRequest = amountOfArticlesPerRequest;
		this.thread = createThread();
		thread.start();
	}

	public void setDaysToDownload(DateTime dayDownloadFrom) {
		this.dayDownloadFrom = dayDownloadFrom;
	}

	public DateTime getDaysToDownload() {
		return dayDownloadFrom;
	}

	public void stopDownload() throws InterruptedException {
		stopped = true;
		if (thread != null) {
			thread.interrupt();
		}
		tasks.add(new Runnable() {
			@Override
			public void run() {
			}
		});
		tasks.clear();
	}

	public boolean isStopped() {
		return stopped;
	}

	void addReceiver(LoadFeedReceiver receiver) {
		receivers.add(receiver);
	}

	public boolean download() {
		boolean result = true;
		int amountOfProcessedArticles = 0;
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
					CallableArticlesDownload.pause();
					amountOfProcessedArticles += getArticles(category, subcategory, dayDownloadFrom);
				} catch (TimeoutException e) {
					logger.error("getArticles returns TimeoutException: " + e.getMessage() + "; we trying to restart executor.");
					updateExecutor();
					result = false;
				} catch (Exception e) {
					logger.error("getArticles returns", e);
					updateExecutor();
					result = false;
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
					+ dayDownloadFrom + ". Which took: " + (endTime - beginTime) + " millisec.");
		}
		logger.info("Received amount of articles: " + amountOfProcessedArticles + " --- for date " + dayDownloadFrom.toString());
		return result;
	}

	int getArticles(final Category category, final Subcategory subcategory, final DateTime startOfDay) throws Exception {
		final FutureTask<Optional<List<Article>>> futureArticles = new FutureTask<>(new CallableArticlesDownload(feed, category,
				subcategory, amountOfArticlesPerRequest, startOfDay));
		tasks.offer(new Runnable() {
			@Override
			public void run() {
				futureArticles.run();
			}
		});
		final long beginArticlesDownloadTime = System.currentTimeMillis();
		final Optional<List<Article>> articles = futureArticles.get(30, TimeUnit.SECONDS);

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
		thread = createThread();
		thread.start();
	}

	private Thread createThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopped) {
					try {
						final Runnable r = tasks.take();
						if (r != null) {
							r.run();
						} else {
							break;
						}
					} catch (Exception e) {
						logger.fatal("Download thread throw an exception: ", e);
					}
				}
			}
		});
	}

}
