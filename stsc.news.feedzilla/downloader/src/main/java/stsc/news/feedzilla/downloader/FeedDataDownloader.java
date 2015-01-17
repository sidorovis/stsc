package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
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
import java.util.concurrent.TimeoutException;

import javax.ws.rs.ForbiddenException;

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

	public static long PAUSE_SLEEP_TIME = 200;

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

	public void startDownload() {
		downloadLastNdays();
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

	private void downloadLastNdays() {
		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusDays(daysToDownload);
		startOfDay = startOfDay.withTimeAtStartOfDay();
		int amountOfProcessedArticles = 0;
		try {

			final List<Category> categories = feed.getCategories();

			for (Category category : categories) {
				final long beginTime = System.currentTimeMillis();
				try {
					pause();
					final List<Subcategory> subcategories = feed.getSubcategories(category);
					for (Subcategory subcategory : subcategories) {
						try {
							pause();
							amountOfProcessedArticles += getArticles(category, subcategory, startOfDay);
						} catch (TimeoutException e) {
							logger.error("getArticles returns TimeoutException, ", e);
						} catch (ForbiddenException e) {
							logger.error("getArticles returns ForbiddenException, ", e);
						} catch (Exception e) {
							logger.error("getArticles returns", e);
						}
						if (stopped) {
							break;
						}
					}
				} catch (Exception e) {
					logger.error("getSubcategories returns", e);
				}
				if (stopped) {
					break;
				}
				final long endTime = System.currentTimeMillis();
				logger.debug("Category " + category.getEnglishName() + " downloaded with " + amountOfProcessedArticles
						+ " articles. For day " + startOfDay + ". Which took: " + (endTime - beginTime) + " millisec.");
			}
		} catch (Exception e) {
			logger.error("getCategories returns", e);
		}
		logger.debug("Received amount of articles: " + amountOfProcessedArticles + ", received new articles: " + hashCodes.size()
				+ " --- for date " + startOfDay.toString());
	}

	int getArticles(final Category category, final Subcategory subcategory, final DateTime startOfDay) throws Exception {
		final FutureTask<Optional<Articles>> futureArticles = new FutureTask<>(new CallableArticlesDownload(logger, feed, category,
				subcategory, amountOfArticlesPerRequest, startOfDay));
		executor.submit(futureArticles);
		final Optional<Articles> articles = futureArticles.get(15, TimeUnit.SECONDS);
		if (!articles.isPresent() || stopped)
			return 0;
		int articlesCount = 0;
		final List<Article> articlesList = articles.get().getArticles();
		for (Article article : articlesList) {
			try {
				final String hashCode = getHashCode(article);
				if (!hashCodes.contains(hashCode)) {
					hashCodes.add(hashCode);
					for (LoadFeedReceiver receiver : receivers) {
						receiver.newArticle(category, subcategory, article);
						articlesCount += 1;
						if (!stopped) {
							return articlesCount;
						}
					}
				}
			} catch (Exception e) {
				logger.error("error while passing article to receiver: for hashcode create: " + article.toString(), e);
			}
			if (!stopped) {
				return articlesCount;
			}
		}
		return articlesList.size();
	}

	private void pause() {
		try {
			Thread.sleep(PAUSE_SLEEP_TIME);
		} catch (Exception e) {
		}
	}

	private static String getHashCode(Article a) {
		return "" + s(a.getAuthor()).hashCode() + " " + s(a.getSource()).hashCode() + " " + s(a.getSummary()).hashCode() + " "
				+ s(a.getTitle()).hashCode();
	}

	private static String s(String v) {
		if (v == null) {
			return "null";
		}
		return v;
	}

}
