package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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
	private final Set<String> hashCodes = new HashSet<>();
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	private volatile boolean stopped = false;

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

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

	public void stopDownload() throws InterruptedException {
		stopped = true;
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
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
		logger.info("Received amount of articles: " + amountOfProcessedArticles + ", received new articles: " + hashCodes.size()
				+ " --- for date " + downloadPeriod.toString());
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
				logger.error("Downloading subcategories throw exception: " + e.getMessage());
			}
			CallableArticlesDownload.pause();
		}
		return Collections.emptyList();
	}

	private DateTime createNextDateTimeElement() {
		return DateTime.now().minusDays(daysToDownload).withTimeAtStartOfDay();
	}

	int getArticles(final Category category, final Subcategory subcategory, final DateTime startOfDay) throws Exception {
		final FutureTask<Optional<List<Article>>> futureArticles = new FutureTask<>(new CallableArticlesDownload(logger, feed, category,
				subcategory, amountOfArticlesPerRequest, startOfDay));
		executor.submit(futureArticles);
		final Optional<List<Article>> articles = futureArticles.get(15, TimeUnit.SECONDS);
		if (!articles.isPresent() || stopped)
			return 0;
		int articlesCount = 0;
		for (Article article : articles.get()) {
			try {
				for (LoadFeedReceiver receiver : receivers) {
					receiver.newArticle(category, subcategory, article);
					articlesCount += 1;
					if (!stopped) {
						return articlesCount;
					}
				}
			} catch (Exception e) {
				logger.fatal("Error while passing article to receiver: for hashcode create: " + article.toString(), e);
			}
			if (!stopped) {
				return articlesCount;
			}
		}
		return articles.get().size();
	}

}
