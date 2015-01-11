package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

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

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
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

	FeedDataDownloader() {
		this(100);
	}

	FeedDataDownloader(int count) {
		this(356 * 20, count);
	}

	FeedDataDownloader(int lastNdays, int count) {
		downloadLastNdays(lastNdays, count);
	}

	private void downloadLastNdays(int N, int count) {
		final Set<String> hashCodes = new HashSet<>();
		final FeedZilla feed = new FeedZilla();

		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusDays(N);
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
						amountOfProcessedArticles += getArticles(feed, hashCodes, category, subcategory, startOfDay, count);
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

	public static int getArticles(FeedZilla feed, Set<String> hashCodes, Category category, Subcategory subcategory, DateTime startOfDay,
			int count) {
		final Articles articles = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay).count(count)
				.articles();
		if (articles == null)
			return 0;
		final List<Article> articlesList = articles.getArticles();
		for (Article article : articlesList) {
			final String hashCode = getHashCode(article);
			if (!hashCodes.contains(hashCode)) {
				hashCodes.add(hashCode);
			}
		}
		return articlesList.size();
	}
//
//	public static void main(String[] args) {
//		try {
//			new FeedDataDownloader(1, 1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
