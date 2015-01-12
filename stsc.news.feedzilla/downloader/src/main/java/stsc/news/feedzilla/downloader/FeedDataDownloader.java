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

import org.joda.time.DateTime;

/**
 * {@link FeedDataDownloader} is a class that download feed's from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	public static long PAUSE_SLEEP_TIME = 100;

	private final int daysToDownload;
	private final int amountOfArticlesPerRequest;
	private List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<LoadFeedReceiver>());

	private final FeedZilla feed = new FeedZilla();
	private final Set<String> hashCodes = new HashSet<>();

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

	void addReceiver(LoadFeedReceiver receiver) {
		receivers.add(receiver);
	}

	private void downloadLastNdays() {
		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusDays(daysToDownload);
		startOfDay = startOfDay.withTimeAtStartOfDay();

		final List<Category> categories = feed.getCategories();
		int amountOfProcessedArticles = 0;

		for (Category category : categories) {
			try {
				pause();
				final List<Subcategory> subcategories = feed.getSubcategories(category);
				for (Subcategory subcategory : subcategories) {
					try {
						pause();
						amountOfProcessedArticles += getArticles(category, subcategory, startOfDay);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Size: (" + amountOfProcessedArticles + ") from " + hashCodes.size());
	}

	int getArticles(Category category, Subcategory subcategory, DateTime startOfDay) {
		final Articles articles = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
				.count(amountOfArticlesPerRequest).articles();
		if (articles == null)
			return 0;
		final List<Article> articlesList = articles.getArticles();
		for (Article article : articlesList) {
			final String hashCode = getHashCode(article);
			if (!hashCodes.contains(hashCode)) {
				hashCodes.add(hashCode);
				for (LoadFeedReceiver receiver : receivers) {
					receiver.newArticle(category, subcategory, article);
				}
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
		return new String(a.getAuthor()).hashCode() + " " + new String(a.getSource()).hashCode() + " "
				+ new String(a.getSummary()).hashCode() + " " + new String(a.getTitle()).hashCode();
	}

}
