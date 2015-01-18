package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class FeedDataDownloaderTest {

	private class ReceiverTestHelper implements LoadFeedReceiver {
		public int sum = 0;

		@Override
		public void newArticle(Category category, Subcategory subcategory, Article article) {
			sum += 1;
		}
	}

	@Test
	public void testFeedDataDownloaderGetArticle() throws Exception {
		DateTime startOfDay = DateTime.now();
		startOfDay = startOfDay.minusDays(1);
		startOfDay = startOfDay.withTimeAtStartOfDay();
		final FeedZilla feed = new FeedZilla();
		final Category category = FeedDataDownloader.getCategories(feed).get(0);
		final Subcategory subcategory = FeedDataDownloader.getSubcategories(feed, category).get(0);
		final ReceiverTestHelper receiver = new ReceiverTestHelper();
		final FeedDataDownloader downloader = new FeedDataDownloader(10, 1);
		downloader.addReceiver(receiver);

		final int articles = downloader.getArticles(category, subcategory, startOfDay);
		Assert.assertEquals(1, articles);
		Assert.assertEquals(1, receiver.sum);
	}

}
