package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.schema.FeedZillaCategory;

public class FeedzillaFileStorageTest {

	@Test
	public void testFeedzillaFileFeedStorage() throws IOException, SQLException {
		final FeedzillaFileStorage fffs = new FeedzillaFileStorage("feedzilla_test.properties");
		Assert.assertNotNull(fffs);
		fffs.addCategory(new FeedZillaCategory("display", "english", null));
		fffs.addCategory(new FeedZillaCategory("hellow", "deutch", "url"));
		fffs.addCategory(new FeedZillaCategory(null, "russian", null));

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
}
