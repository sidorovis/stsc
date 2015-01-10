package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.schema.FeedZillaArticle;
import stsc.news.feedzilla.schema.FeedZillaCategory;
import stsc.news.feedzilla.schema.FeedZillaSubcategory;

public class FeedzillaStorageTest {

	@Test
	public void testFeedzillaFileFeedStorage() throws IOException, SQLException {
		final FeedzillaStorage fffs = new FeedzillaStorage("feedzilla_test.properties");
		Assert.assertNotNull(fffs);
	}

	@Test
	public void testFeedZillaCategory() throws IOException, SQLException {
		final FeedzillaStorage fffs = new FeedzillaStorage("feedzilla_test.properties");
		fffs.createOrUpdateCategory(new FeedZillaCategory("display", "english", null));
		fffs.createOrUpdateCategory(new FeedZillaCategory("hellow", "deutch", "url"));
		fffs.createOrUpdateCategory(new FeedZillaCategory(null, "russian", null));

		final List<FeedZillaCategory> categories = fffs.getCategories();
		Assert.assertEquals(3, categories.size());
		Assert.assertEquals("display", categories.get(0).getDisplayCategoryName());
		Assert.assertEquals("english", categories.get(0).getEnglishCategoryName());
		Assert.assertNull(categories.get(0).getUrlCategoryName());

		Assert.assertEquals("hellow", categories.get(1).getDisplayCategoryName());
		Assert.assertEquals("deutch", categories.get(1).getEnglishCategoryName());
		Assert.assertEquals("url", categories.get(1).getUrlCategoryName());

		Assert.assertNull(categories.get(2).getDisplayCategoryName());
		Assert.assertEquals("russian", categories.get(2).getEnglishCategoryName());
		Assert.assertNull(categories.get(2).getUrlCategoryName());

		fffs.dropAllCategories();
	}

	@Test
	public void testFeedZillaSubcategory() throws IOException, SQLException {
		final FeedzillaStorage fffs = new FeedzillaStorage("feedzilla_test.properties");
		final FeedZillaCategory category = new FeedZillaCategory("display", "english", null);
		final FeedZillaSubcategory subcategory = new FeedZillaSubcategory(category, "subdisplay", "subenglish", "sub-url");
		fffs.createOrUpdateCategory(category);
		fffs.createOrUpdateSubcategory(subcategory);
		fffs.dropAllCategories();
	}

	@Test
	public void testFeedZillaArticle() throws IOException, SQLException {
		final FeedzillaStorage fffs = new FeedzillaStorage("feedzilla_test.properties");
		final FeedZillaCategory category = new FeedZillaCategory("display", "english", null);
		final FeedZillaSubcategory subcategory = new FeedZillaSubcategory(category, "subdisplay", "subenglish", "sub-url");
		final FeedZillaArticle article = new FeedZillaArticle(subcategory, "author", new Date());
		article.setSummary("Summary");
		fffs.createOrUpdateCategory(category);
		fffs.createOrUpdateSubcategory(subcategory);
		fffs.createOrUpdateArticle(article);
		fffs.dropAllCategories();
	}
}
