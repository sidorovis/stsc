package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

		final Collection<FeedzillaOrmliteCategory> categories = fffs.getCategories();
		Assert.assertEquals(3, categories.size());

		final Iterator<FeedzillaOrmliteCategory> i = categories.iterator();
		final FeedzillaOrmliteCategory c0 = i.next();
		final FeedzillaOrmliteCategory c1 = i.next();
		final FeedzillaOrmliteCategory c2 = i.next();

		Assert.assertEquals("display", c0.getDisplayCategoryName());
		Assert.assertEquals("english", c0.getEnglishCategoryName());
		Assert.assertNull(c0.getUrlCategoryName());

		Assert.assertEquals("hellow", c1.getDisplayCategoryName());
		Assert.assertEquals("deutch", c1.getEnglishCategoryName());
		Assert.assertEquals("url", c1.getUrlCategoryName());

		Assert.assertNull(c2.getDisplayCategoryName());
		Assert.assertEquals("russian", c2.getEnglishCategoryName());
		Assert.assertNull(c2.getUrlCategoryName());

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
