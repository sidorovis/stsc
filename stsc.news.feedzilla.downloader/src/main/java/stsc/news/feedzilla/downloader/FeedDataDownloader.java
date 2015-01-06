package stsc.news.feedzilla.downloader;

import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

/**
 * {@link FeedDataDownloader} is a class that download newses from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	FeedDataDownloader() {

	}

	public static void main(String[] args) {
		try {
			new FeedDataDownloader();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void gFeedDataDownloader2() {
		FeedZilla feed = new FeedZilla();

		for (Subcategory s : feed.getSubcategories()) {
			System.out.println(s.getUrlName());
		}

		// for (Category c : feed.getCategories()) {
		// try {
		// System.out.print(c + ": ");
		// System.out.println(feed.query().since(new DateTime(1950, 1, 1, 0,
		// 0)).category(c).articles().getArticles().size());
		// } catch (Exception e) {
		// System.out.println("E!: " + e.getMessage());
		// }
		// }

		// Articles articles = feed.query().category(28).count(5).articles();
		// articles.getArticles().get(0).
		//
		// for (Article a : articles.getArticles()) {
		// System.out.println(a.getTitle());
		// }
		// System.out.println();

		// for (Subcategory c : feed.getSubcategories()) {
		// System.out.println(c);
		// }
		// Articles articles = feed.getCategories();
		// query().count(10).articles();
		//
		// for (Article a : articles.getArticles()) {
		// System.out.println(a.getAuthor());
		// System.out.println(a.getSource());
		// System.out.println(a.getSummary());
		// System.out.println(a.getPublishDate());
		// }
	}
}
