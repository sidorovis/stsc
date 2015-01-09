package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.HashMap;
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
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}

	FeedDataDownloader() {
		// DateTime startOfDay = DateTime.now();
		// startOfDay = startOfDay.minusYears(15);
		// startOfDay = startOfDay.withTimeAtStartOfDay();
		final FeedZilla feed = new FeedZilla();
		// int i = 0;
		// final HashMap<String, Article> map = new HashMap<>();

		int maxDsn = 0;
		int maxEsn = 0;
		int maxUsn = 0;

		final List<Subcategory> categories = feed.getSubcategories();
		for (Subcategory subcategory : categories) {
			System.out.println(subcategory.getParentId());
			maxDsn = Math.max(maxDsn, subcategory.getDisplayName().length());
			maxEsn = Math.max(maxDsn, subcategory.getEnglishName().length());
			maxUsn = Math.max(maxDsn, subcategory.getUrlName().length());
		}
		System.out.println("-----------");
		System.out.println(maxDsn);
		System.out.println(maxEsn);
		System.out.println(maxUsn);

		// for (Category c : categories) {
		// System.out.println(c.getId());
		// System.out.println(c.getDisplayName());
		// System.out.println(c.getEnglishName());
		// System.out.println(c.getUrlName());
		// }
		// final List<Subcategory> subcategories = feed.getSubcategories(c);
		// System.out.println(" - " + subcategories.size());
		// pause();
		// for (Subcategory s : subcategories) {
		// try {
		// final Articles articles =
		// feed.query().category(c).subcategory(s).since(startOfDay).count(100).articles();
		// if (articles == null) {
		// System.out.println("--------- -------------");
		// } else {
		// System.out.println("Size: " + articles.getArticles().size());
		// // for (Article article : articles.getArticles()) {
		// // i++;
		// // map.put(article.getTitle(), article);
		// // pause();
		// // if (i % 1000 == 0) {
		// // System.out.println("articles " + i);
		// // }
		// // }
		// }
		// } catch (Exception e) {
		// System.err.println(i++ + " " + e.getMessage());
		// }
		// }
		// }
		// System.out.println("Size is: " + i + " by map: " + map.size());

	}

	public static void main(String[] args) {
		try {
			new FeedDataDownloader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
