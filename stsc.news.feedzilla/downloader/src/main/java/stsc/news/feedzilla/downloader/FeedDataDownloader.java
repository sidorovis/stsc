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
//
//import org.javalite.activejdbc.Base;
//import org.joda.time.DateTime;

/**
 * {@link FeedDataDownloader} is a class that download feed's from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	void pause() {
		try{
			Thread.sleep(100);
		} catch (Exception e) {
			
		}
	}

	String getHashCode(Article a) {
		return new String(a.getAuthor()).hashCode() + " " + new String(a.getSource()).hashCode() + " "
				+ new String(a.getSummary()).hashCode() + " " + new String(a.getTitle()).hashCode();
	}

	FeedDataDownloader() {
		Set<String> hashCodes = new HashSet<>();

		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusYears(20);
		startOfDay = startOfDay.withTimeAtStartOfDay();
		final FeedZilla feed = new FeedZilla();

		final List<Category> categories = feed.getCategories();

		int i = 0;

		for (Category category : categories) {
			try {
				pause();
				final List<Subcategory> subcategories = feed.getSubcategories(category);
				for (Subcategory subcategory : subcategories) {
					try {
						pause();
						final Articles articles = feed.query().subcategory(subcategory).since(startOfDay).count(100)
								.articles();
						if (articles == null)
							break;
						final List<Article> articlesList = articles.getArticles();
						for (Article article : articlesList) {
							i++;
							final String hashCode = getHashCode(article);
							if (hashCodes.contains(hashCode)) {
								System.err.println("Repeat: " + article.getPublishDate() + " " + article.getTitle());
							} else {
								hashCodes.add(hashCode);
							}
							if (i % 500 == 0) {
								System.out.println("Processed: " + i + " articles");
							}
							pause();
						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Size: " + hashCodes.size());
	}

	public static void main(String[] args) {
		try {
			new FeedDataDownloader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
