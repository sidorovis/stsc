package stsc.news.feedzilla.downloader;

import java.util.Collection;
import java.util.List;

import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.Culture;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

/**
 * {@link FeedDataDownloader} is a class that download newses from FeedZilla and
 * categories them.
 */
final class FeedDataDownloader {

	FeedDataDownloader() {
		FeedZilla feed = new FeedZilla();
		int i = 0;
		final List<Category> categories = feed.getCategories();
		final List<Subcategory> subcategories = feed.getSubcategories();
		final Collection<Culture> cultures = feed.getCultures();
		for (Category cgr : categories) {
			for (Subcategory scgr : subcategories) {
//				for (Culture cltr : cultures) {
					i++;
//					try {
//						final Articles articles = feed.query().category(cgr).subcategory(scgr).culture(cltr).articles();
//						if (articles == null) {
//							continue;
//						}
//						System.out.println(i++ + " " + articles.getDescription());
//					} catch (Exception e) {
//						System.err.println(i++ + " " + e.getMessage());
//					}
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
//		}
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
