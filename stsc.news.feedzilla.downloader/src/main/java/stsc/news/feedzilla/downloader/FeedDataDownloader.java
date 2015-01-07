package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.Culture;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;

/**
 * {@link FeedDataDownloader} is a class that download newses from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private void pause() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	FeedDataDownloader() {
		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusYears(15);
		startOfDay = startOfDay.withTimeAtStartOfDay();
		final FeedZilla feed = new FeedZilla();
		int i = 0;
		final List<Category> categories = feed.getCategories();
		for (Category cgr : categories) {
			try {
				final Articles articles = feed.query().category(cgr).since(startOfDay).count(100).articles();
				i++;
				if (articles == null) {

				} else {
					for (Article article : articles.getArticles()) {
						System.out.println(article.getPublishDate());
						// System.out.println(article.getSummary());
						pause();
					}

				}
			} catch (Exception e) {
				System.err.println(i++ + " " + e.getMessage());
			}
		}
		// }
		System.out.println(i);

	}

	public static void main(String[] args) {
		try {
			new FeedDataDownloader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
