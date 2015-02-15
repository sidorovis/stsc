package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class FeedDataDownloaderTest {

	private final Logger logger = LogManager.getLogger(FeedDataDownloaderTest.class);

	private class ReceiverTestHelper implements LoadFeedReceiver {
		public int sum = 0;

		@Override
		public void newArticle(Category category, Subcategory subcategory, Article article) {
			sum += 1;
		}
	}

	@Test
	public void testFeedDataDownloaderGetArticle() throws Exception {
		LocalDateTime startOfDay = LocalDateTime.now().minusDays(5).withHour(0).withMinute(0);
		final FeedZilla feed = new FeedZilla();
		final Category category = DownloadHelper.getCategories(feed, logger).get(0);
		final Subcategory subcategory = DownloadHelper.getSubcategories(feed, category, logger).get(0);
		final ReceiverTestHelper receiver = new ReceiverTestHelper();
		final FeedDataDownloader downloader = new FeedDataDownloader(LocalDateTime.now().minusDays(10), 1, 20);
		downloader.addReceiver(receiver);

		final int articles = downloader.getArticles(category, subcategory, startOfDay, 1);
		Assert.assertEquals(1, articles);
		Assert.assertEquals(1, receiver.sum);
	}
}
