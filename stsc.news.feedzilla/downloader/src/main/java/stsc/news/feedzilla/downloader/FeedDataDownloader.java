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
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
	}

	private static Logger logger = LogManager.getLogger(FeedDataDownloader.class);

	public static long PAUSE_SLEEP_TIME = 200;

	private int daysToDownload;
	private final int amountOfArticlesPerRequest;
	private List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadFeedReceiver>());

	private final FeedZilla feed = new FeedZilla();
	private final Set<String> hashCodes = new HashSet<>();
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

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

	public void stopDownload() {
		stopped = true;
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

			logger.debug("Downloading process started");
			for (Category category : categories) {
				try {
					logger.debug("We start download category: " + category.getDisplayName());
					pause();
					final List<Subcategory> subcategories = feed.getSubcategories(category);
					for (Subcategory subcategory : subcategories) {
						try {
							logger.debug("We start download subcategory: " + subcategory.getDisplayName());
							pause();
							amountOfProcessedArticles += getArticles(category, subcategory, startOfDay);

						} catch (TimeoutException e) {
							logger.error("getArticles returns TimeoutException:" + e.getMessage());
						} catch (ForbiddenException e) {
							logger.error("getArticles returns ForbiddenException:" + e.getMessage());
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
			}
		} catch (Exception e) {
			logger.error("getCategories returns", e);
		}
		logger.debug("Received amount of articles: " + amountOfProcessedArticles + ", received new articles: " + hashCodes.size());
	}

	int getArticles(Category category, Subcategory subcategory, DateTime startOfDay) throws InterruptedException, ExecutionException,
			TimeoutException {
		final FutureTask<Articles> futureArticles = new FutureTask<>(new Callable<Articles>() {
			@Override
			public Articles call() throws Exception {
				try {
					final Articles result = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
							.count(amountOfArticlesPerRequest).articles();
					return result;
				} catch (Exception e) {
					logger.error("article hashcode create: "
							+ feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
									.count(amountOfArticlesPerRequest).articles());
				}
				return null;
			}
		});
		executor.execute(futureArticles);
		final Articles articles = futureArticles.get(5, TimeUnit.SECONDS);
		if (articles == null)
			return 0;
		int articlesCount = 0;
		final List<Article> articlesList = articles.getArticles();
		logger.debug("We will download " + articlesList.size() + " articles for " + category.getDisplayName() + " category and "
				+ subcategory.getDisplayName() + " subcategory.");
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
				logger.error("article hashcode create: " + article.toString(), e);
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
