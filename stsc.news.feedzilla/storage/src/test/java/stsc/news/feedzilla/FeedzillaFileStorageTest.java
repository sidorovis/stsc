package stsc.news.feedzilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaFileStorageTest {

	class FileStorageReceiver implements FeedzillaFileStorageReceiver {

		public int size = 0;

		@Override
		public boolean addArticle(FeedzillaFileArticle article) {
			size += 1;
			return true;
		}

	}

	@Test
	public void testFeedzillaFileStorageController() throws FileNotFoundException, IOException {
		final String feedFolder = FeedzillaFileStorageTest.class.getResource("").getPath();
		FeedzillaFileSaver.saveCategories(feedFolder, Collections.emptyMap());
		FeedzillaFileSaver.saveSubcategories(feedFolder, Collections.emptyMap());
		FeedzillaFileSaver.saveArticles(feedFolder, Collections.emptyList());
		{
			final FileStorageReceiver r = new FileStorageReceiver();
			final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, LocalDateTime.now().minusDays(3650), true);
			storage.addReceiver(r);
			storage.readData();
			Assert.assertNotNull(storage);
			Assert.assertTrue(storage.getCategories().isEmpty());
			Assert.assertTrue(storage.getSubcategories().isEmpty());
			Assert.assertTrue(storage.getArticlesById().isEmpty());

			final Map<String, FeedzillaFileCategory> categories = new HashMap<>();
			categories.put("key", new FeedzillaFileCategory(14, "test", "english", null));
			FeedzillaFileSaver.saveCategories(feedFolder, categories);

			final Map<String, FeedzillaFileSubcategory> subcategories = new HashMap<>();
			subcategories.put("key", new FeedzillaFileSubcategory(14, categories.get("key"), "test", "english", null));
			FeedzillaFileSaver.saveSubcategories(feedFolder, subcategories);

			final List<FeedzillaFileArticle> articles = new ArrayList<>();
			articles.add(new FeedzillaFileArticle(56, subcategories.get("key"), null, LocalDateTime.now()));
			FeedzillaFileSaver.saveArticles(feedFolder, articles);
		}
		{
			final FileStorageReceiver r = new FileStorageReceiver();
			final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, LocalDateTime.now().minusDays(3650), true);
			storage.addReceiver(r);
			storage.readData();
			Assert.assertNotNull(storage);
			Assert.assertEquals(1, storage.getCategories().size());
			Assert.assertEquals(1, storage.getSubcategories().size());
			Assert.assertEquals(1, storage.getArticlesById().size());
		}
		Assert.assertTrue(new File(feedFolder + "/_categories" + FeedzillaFileSaver.FILE_EXTENSION).delete());
		Assert.assertTrue(new File(feedFolder + "/_subcategories" + FeedzillaFileSaver.FILE_EXTENSION).delete());
		for (String articleName : FeedzillaFileStorage.readFileList(feedFolder)) {
			final String path = feedFolder + "/" + articleName + FeedzillaFileSaver.FILE_ARTICLE_EXTENSION;
			Assert.assertTrue(new File(path).delete());
		}
	}

	@Test
	public void testFeedzillaFileStorageLoadTest() throws FileNotFoundException, IOException {
		final String feedFolder = "./../test_data/feed_data";
		final FileStorageReceiver r = new FileStorageReceiver();
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, LocalDateTime.now().minusDays(3650), true);
		storage.addReceiver(r);
		storage.readData();
		Assert.assertNotNull(storage);
		Assert.assertEquals(36, storage.getCategories().size());
		Assert.assertEquals(600, storage.getSubcategories().size());
		Assert.assertEquals(1336, storage.getArticlesById().size());
	}

	@Test
	public void testFeedzillaFileStorageLoadTestWhereAllArticlesAreOld() throws FileNotFoundException, IOException {
		final String feedFolder = "./../test_data/feed_data";
		final FileStorageReceiver r = new FileStorageReceiver();
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, LocalDateTime.now(), true);
		storage.addReceiver(r);
		storage.readData();
		Assert.assertNotNull(storage);
		Assert.assertEquals(36, storage.getCategories().size());
		Assert.assertEquals(600, storage.getSubcategories().size());
		Assert.assertEquals(0, storage.getArticlesById().size());
	}
}
