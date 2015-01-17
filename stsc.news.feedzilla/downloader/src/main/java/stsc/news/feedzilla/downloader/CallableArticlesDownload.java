package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

class CallableArticlesDownload implements Callable<Optional<Articles>> {

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
	public Optional<Articles> call() throws Exception {
		try {
			final Articles result = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
					.count(amountOfArticlesPerRequest).articles();
			return Optional.of(result);
		} catch (Exception e) {
			logger.error("download failed at article hashcode create: " + category.getId() + " subcategory " + subcategory.getId() + " "
					+ e.getMessage());
		}
		return Optional.empty();
	}
}
