package stsc.news.feedzila;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;

public class FeedzilaNewsDatafeed {

	public FeedzilaNewsDatafeed() {
		FeedZilla feed = new FeedZilla();
		Articles articles = feed.query().category(28).count(5).articles();

		for (Article a : articles.getArticles()) {
			System.out.println(a.getTitle());
		}
		System.out.println();
		for (Category c : feed.getCategories()) {
			System.out.println(c);
		}

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
