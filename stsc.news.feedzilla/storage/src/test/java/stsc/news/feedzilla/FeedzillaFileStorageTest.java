package stsc.news.feedzilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaFileStorageTest {

	@Test
	public void testFeedzillaFileStorageController() throws FileNotFoundException, IOException {
		final String feedFolder = FeedzillaFileStorageTest.class.getResource("").getPath();
		FeedzillaFileStorage.saveCategories(feedFolder, Collections.emptyMap());
		FeedzillaFileStorage.saveSubcategories(feedFolder, Collections.emptyMap());
		FeedzillaFileStorage.saveArticles(feedFolder, Collections.emptyList());
		{
			final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder);
			Assert.assertNotNull(storage);
			Assert.assertTrue(storage.getCategories().isEmpty());
			Assert.assertTrue(storage.getSubcategories().isEmpty());
			Assert.assertTrue(storage.getArticles().isEmpty());

			final Map<String, FeedzillaFileCategory> categories = new HashMap<>();
			categories.put("key", new FeedzillaFileCategory(14, "test", "english", null));
			FeedzillaFileStorage.saveCategories(feedFolder, categories);

			final Map<String, FeedzillaFileSubcategory> subcategories = new HashMap<>();
			subcategories.put("key", new FeedzillaFileSubcategory(14, categories.get("key"), "test", "english", null));
			FeedzillaFileStorage.saveSubcategories(feedFolder, subcategories);

			final List<FeedzillaFileArticle> articles = new ArrayList<>();
			articles.add(new FeedzillaFileArticle(56, subcategories.get("key"), null, new Date()));
			FeedzillaFileStorage.saveArticles(feedFolder, articles);
		}
		{
			final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder);
			Assert.assertNotNull(storage);
			Assert.assertEquals(1, storage.getCategories().size());
			Assert.assertEquals(1, storage.getSubcategories().size());
			Assert.assertEquals(1, storage.getArticles().size());
		}
		Assert.assertTrue(new File(feedFolder + "/_categories" + FeedzillaFileStorage.FILE_EXTENSION).delete());
		Assert.assertTrue(new File(feedFolder + "/_subcategories" + FeedzillaFileStorage.FILE_EXTENSION).delete());
		for (String articleName : FeedzillaFileStorage.readFileList(feedFolder)) {
			final String path = feedFolder + "/" + articleName + FeedzillaFileStorage.FILE_ARTICLE_EXTENSION;
			Assert.assertTrue(new File(path).delete());
		}
	}

	@Test
	public void testFeedzillaFileStorageLoadTest() throws FileNotFoundException, IOException {
		final String feedFolder = "./../test_data/";
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder);
		Assert.assertNotNull(storage);
		Assert.assertEquals(36, storage.getCategories().size());
		Assert.assertEquals(600, storage.getSubcategories().size());
		Assert.assertEquals(769, storage.getArticles().size());
	}
}
