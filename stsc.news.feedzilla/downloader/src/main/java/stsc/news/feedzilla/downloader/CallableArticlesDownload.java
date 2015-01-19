package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

class CallableArticlesDownload implements Callable<Optional<List<Article>>> {

	public static final int TRIES_COUNT = 5;
	public static final long PAUSE_SLEEP_TIME = 200;

	private final Logger logger;

	private final FeedZilla feed;
	private final Category category;
	private final Subcategory subcategory;
	private final int amountOfArticlesPerRequest;
	private final DateTime startOfDay;

	public CallableArticlesDownload(final Logger logger, FeedZilla feed, Category category, Subcategory subcategory,
			int amountOfArticlesPerRequest, DateTime startOfDay) {
		super();
		this.logger = logger;
		this.feed = feed;
		this.category = category;
		this.subcategory = subcategory;
		this.amountOfArticlesPerRequest = amountOfArticlesPerRequest;
		this.startOfDay = startOfDay;
	}

	@Override
	public Optional<List<Article>> call() throws Exception {
		for (int amountOfTries = 0; amountOfTries < TRIES_COUNT; ++amountOfTries) {
			try {
				final Articles articles = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
						.count(amountOfArticlesPerRequest).articles();
				final List<Article> articlesList = articles.getArticles();
				return Optional.of(articlesList);
			} catch (Exception e) {
				logger.error("Article download for (" +startOfDay+ "): " + e.getMessage());
			}
			pause();
		}
		return Optional.empty();
	}

	public static void pause() {
		try {
			Thread.sleep(PAUSE_SLEEP_TIME);
		} catch (Exception e) {
		}
	}

}
