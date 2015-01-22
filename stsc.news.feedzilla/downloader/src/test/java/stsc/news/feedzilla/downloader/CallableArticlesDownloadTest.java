package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class CallableArticlesDownloadTest {

	@Test
	public void testCallableArticlesDownload() throws Exception {
		final FeedZilla feed = new FeedZilla();
		final Category c = FeedDataDownloader.getCategories(feed).get(0);
		final Subcategory s = FeedDataDownloader.getSubcategories(feed, c).get(0);
		final CallableArticlesDownload callable = new CallableArticlesDownload(feed, c, s, 1, new DateTime().minusDays(10));

		final Optional<List<Article>> articles = callable.call();
		Assert.assertTrue(articles.isPresent());
		Assert.assertFalse(articles.get().isEmpty());
	}
}
