package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class CallableArticlesDownloadTest {

	private final Logger logger = LogManager.getLogger(CallableArticlesDownloadTest.class);

	@Test
	public void testCallableArticlesDownload() throws Exception {
		final FeedZilla feed = new FeedZilla();
		final Category c = DownloadHelper.getCategories(feed, logger).get(0);
		final Subcategory s = DownloadHelper.getSubcategories(feed, c, logger).get(0);
		final CallableArticlesDownload callable = new CallableArticlesDownload(feed, c, s, 1, LocalDateTime.now().minusDays(10));

		final Optional<List<Article>> articles = callable.call();
		Assert.assertTrue(articles.isPresent());
		Assert.assertFalse(articles.get().isEmpty());
	}
}
