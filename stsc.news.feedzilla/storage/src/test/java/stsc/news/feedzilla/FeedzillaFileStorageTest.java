package stsc.news.feedzilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class FeedzillaFileStorageTest {

	@Test
	public void testFeedzillaFileStorage() throws FileNotFoundException, IOException {
		final String feedFolder = "./../test_data/";
		FeedzillaFileStorage.saveCategories(feedFolder, Collections.emptyMap());
		FeedzillaFileStorage.saveSubcategories(feedFolder, Collections.emptyMap());
		FeedzillaFileStorage.saveArticles(feedFolder, Collections.emptyList());
		final FeedzillaFileStorage storage = new FeedzillaFileStorage("./../test_data");
		Assert.assertNotNull(storage);
		Assert.assertTrue(storage.getCategories().isEmpty());
		Assert.assertTrue(storage.getSubcategories().isEmpty());
		Assert.assertTrue(storage.getArticles().isEmpty());
		Assert.assertTrue(new File(feedFolder + "/_categories" + FeedzillaFileStorage.FILE_EXTENSION).delete());
		Assert.assertTrue(new File(feedFolder + "/_subcategories" + FeedzillaFileStorage.FILE_EXTENSION).delete());
		for (String articleName : FeedzillaFileStorage.readFileList(feedFolder)) {
			final String path = feedFolder + "/" + articleName + FeedzillaFileStorage.FILE_ARTICLE_EXTENSION;
			Assert.assertTrue(new File(path).delete());
		}
	}
}
