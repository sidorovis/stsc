package stsc.news.feedzilla;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.feeds.FeedStorageHelper;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaHashStorage {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedzillaHashStorage.class);

	private final String feedFolder;

	private Map<String, FeedzillaFileCategory> hashCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaFileSubcategory> hashSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaFileArticle> hashArticles = Collections.synchronizedMap(new HashMap<>());

	private int lastStoredCategoriesAmount = 0;
	private int lastStoredSubcategoriesAmount = 0;
	private int lastStoredArticlesAmount = 0;
	private List<FeedzillaFileArticle> newArticles = Collections.synchronizedList(new ArrayList<>());

	public FeedzillaHashStorage(String feedFolder) {
		this.feedFolder = feedFolder;
	}

	public void initialReadFeedData() throws FileNotFoundException, IOException {
		logger.info("Start to create hashcode for database");
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder);
		for (FeedzillaFileCategory c : storage.getCategories()) {
			hashCategories.put(FeedStorageHelper.createHashCode(c), c);
		}
		lastStoredCategoriesAmount = storage.getCategories().size();
		for (FeedzillaFileSubcategory s : storage.getSubcategories()) {
			hashSubcategories.put(FeedStorageHelper.createHashCode(s), s);
		}
		lastStoredSubcategoriesAmount = storage.getSubcategories().size();
		for (FeedzillaFileArticle a : storage.getArticles()) {
			hashArticles.put(FeedStorageHelper.createHashCode(a), a);
		}
		lastStoredArticlesAmount = storage.getArticles().size();
		logger.info("Hashcode created. Categories: " + lastStoredCategoriesAmount + ". Subcategories: " + lastStoredSubcategoriesAmount
				+ ". Articles: " + lastStoredArticlesAmount);
	}

	public void save() throws FileNotFoundException, IOException {
		if (hashCategories.size() != lastStoredCategoriesAmount) {
			saveCategories();
		}
		if (hashSubcategories.size() != lastStoredSubcategoriesAmount) {
			saveSubcategories();
		}
		if (hashArticles.size() != lastStoredArticlesAmount) {
			saveArticles();
		}
		logger.info("Download iteration finished. Categories: " + lastStoredCategoriesAmount + ". Subcategories: "
				+ lastStoredSubcategoriesAmount + ". Articles: " + lastStoredArticlesAmount);
	}

	private void saveCategories() throws FileNotFoundException, IOException {
		synchronized (hashCategories) {
			FeedzillaFileStorage.saveCategories(feedFolder, hashCategories);
			lastStoredCategoriesAmount = hashCategories.size();
		}
	}

	private void saveSubcategories() throws FileNotFoundException, IOException {
		synchronized (hashSubcategories) {
			FeedzillaFileStorage.saveSubcategories(feedFolder, hashSubcategories);
			lastStoredSubcategoriesAmount = hashSubcategories.size();
		}
	}

	private void saveArticles() throws FileNotFoundException, IOException {
		synchronized (hashArticles) {
			FeedzillaFileStorage.saveArticles(feedFolder, newArticles);
			newArticles.clear();
			lastStoredArticlesAmount = hashArticles.size();
		}
	}

	public FeedzillaFileCategory createFeedzillaCategory(FeedzillaFileCategory result) {
		synchronized (hashCategories) {
			final int id = hashCategories.size();
			result.setId(id);
			final String hashCode = FeedStorageHelper.createHashCode(result);
			final FeedzillaFileCategory oldCategory = hashCategories.putIfAbsent(hashCode, result);
			if (oldCategory != null) {
				return oldCategory;
			}
			return result;
		}
	}

	public FeedzillaFileSubcategory createFeedzillaSubcategory(FeedzillaFileCategory category, FeedzillaFileSubcategory result) {
		synchronized (hashSubcategories) {
			final int id = hashSubcategories.size();
			result.setId(id);
			final String hashCode = FeedStorageHelper.createHashCode(result);
			final FeedzillaFileSubcategory oldSubcategory = hashSubcategories.putIfAbsent(hashCode, result);
			if (oldSubcategory != null) {
				return oldSubcategory;
			}
			return result;
		}
	}

	public void createFeedzillaArticle(FeedzillaFileSubcategory subcategory, FeedzillaFileArticle result) {
		synchronized (hashArticles) {
			final int id = hashArticles.size();
			result.setId(id);
			final String hashCode = FeedStorageHelper.createHashCode(result);
			final FeedzillaFileArticle oldArticle = hashArticles.putIfAbsent(hashCode, result);
			if (oldArticle == null) {
				newArticles.add(result);
			}
		}
	}

}
