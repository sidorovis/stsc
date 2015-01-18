package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteArticle;
import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteCategory;
import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteSubcategory;

public class FeedzillaOrmliteStorageTest {

	@Test
	public void testFeedzillaFileFeedStorage() throws IOException, SQLException {
		final FeedzillaOrmliteStorage fffs = new FeedzillaOrmliteStorage("feedzilla_test.properties");
		Assert.assertNotNull(fffs);
	}

	@Test
	public void testFeedzillaOrmliteCategory() throws IOException, SQLException {
		final FeedzillaOrmliteStorage fffs = new FeedzillaOrmliteStorage("feedzilla_test.properties");
		Assert.assertEquals(1, fffs.createOrUpdateCategory(new FeedzillaOrmliteCategory("display", "english", null)));
		Assert.assertEquals(1, fffs.createOrUpdateCategory(new FeedzillaOrmliteCategory("hellow", "deutch", "url")));
		Assert.assertEquals(1, fffs.createOrUpdateCategory(new FeedzillaOrmliteCategory(null, "russian", null)));

		final List<FeedzillaOrmliteCategory> categories = fffs.getCategories();
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
	public void testFeedzillaOrmliteSubcategory() throws IOException, SQLException {
		final FeedzillaOrmliteStorage fffs = new FeedzillaOrmliteStorage("feedzilla_test.properties");
		final FeedzillaOrmliteCategory category = new FeedzillaOrmliteCategory("display", "english", null);
		final FeedzillaOrmliteSubcategory subcategory = new FeedzillaOrmliteSubcategory(category, "subdisplay", "subenglish", "sub-url");
		Assert.assertEquals(1, fffs.createOrUpdateCategory(category));
		Assert.assertEquals(1, fffs.createOrUpdateSubcategory(subcategory));
		fffs.dropAllCategories();
	}

	@Test
	public void testFeedzillaOrmliteArticle() throws IOException, SQLException {
		final FeedzillaOrmliteStorage fffs = new FeedzillaOrmliteStorage("feedzilla_test.properties");
		final FeedzillaOrmliteCategory category = new FeedzillaOrmliteCategory("display", "english", null);
		final FeedzillaOrmliteSubcategory subcategory = new FeedzillaOrmliteSubcategory(category, "subdisplay", "subenglish", "sub-url");
		final FeedzillaOrmliteArticle article = new FeedzillaOrmliteArticle(subcategory, "author", new Date());
		article.setSummary("Summary");
		Assert.assertEquals(1, fffs.createOrUpdateCategory(category));
		Assert.assertEquals(1, fffs.createOrUpdateSubcategory(subcategory));
		Assert.assertEquals(1, fffs.createOrUpdateArticle(article));
		fffs.dropAllCategories();
	}
}
