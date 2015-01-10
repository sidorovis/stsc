package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.javalite.activejdbc.Base;
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

	private void openDatabase() {
		Base.open("org.sqlite.JDBC", "jdbc:sqlite:./../test_data/feedzilla_developer.s3db", "", "");
	}

	FeedDataDownloader() {

		openDatabase();

		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusDays(200);
		startOfDay = startOfDay.withTimeAtStartOfDay();
		final FeedZilla feed = new FeedZilla();

		int maxDsn = 0;

		final List<Category> categories = feed.getCategories();
		Base.openTransaction();
		for (Category category : categories) {
			stsc.news.feedzilla.schema.Category c = new stsc.news.feedzilla.schema.Category();
			c.set("display_category_name", category.getDisplayName());
			c.set("english_category_name", category.getEnglishName());
			c.saveIt();
		}
		Base.commitTransaction();
		Base.close();
		System.out.println("-----------");
		System.out.println(maxDsn);

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

	}

	public static void main(String[] args) {
		try {
			new FeedDataDownloader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
