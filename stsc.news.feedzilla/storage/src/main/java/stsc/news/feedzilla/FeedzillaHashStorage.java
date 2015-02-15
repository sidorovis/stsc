package stsc.news.feedzilla;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.feeds.FeedStorageHelper;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaHashStorage implements FeedzillaFileStorageReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedzillaHashStorage.class);

	private final String feedFolder;

	private Map<String, FeedzillaFileCategory> hashCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaFileSubcategory> hashSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Set<String> hashArticles = Collections.synchronizedSet(new HashSet<>());

	private int lastStoredCategoriesAmount = 0;
	private int lastStoredSubcategoriesAmount = 0;
	private int lastStoredArticlesAmount = 0;
	private List<FeedzillaFileArticle> newArticles = Collections.synchronizedList(new ArrayList<>());

	private final List<FeedzillaHashStorageReceiver> receivers = new ArrayList<>();

	public FeedzillaHashStorage(String feedFolder) {
		this.feedFolder = feedFolder;
	}

	public void addReceiver(FeedzillaHashStorageReceiver receiver) {
		receivers.add(receiver);
	}

	public FeedzillaFileStorage readFeedDataAndStore(LocalDateTime datetime) throws FileNotFoundException, IOException {
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, datetime, true);
		storage.addReceiver(this);
		storage.readData();
		return readFeedData(storage);
	}

	public FeedzillaFileStorage readFeedData(LocalDateTime datetime) throws FileNotFoundException, IOException {
		final FeedzillaFileStorage storage = new FeedzillaFileStorage(feedFolder, datetime, false);
		storage.addReceiver(this);
		storage.readData();
		return readFeedData(storage);
	}

	public FeedzillaFileStorage readFeedData(FeedzillaFileStorage storage) throws FileNotFoundException, IOException {
		for (FeedzillaFileCategory c : storage.getCategories()) {
			putCategory(FeedStorageHelper.createHashCode(c), c);
		}
		lastStoredCategoriesAmount = storage.getCategories().size();
		for (FeedzillaFileSubcategory s : storage.getSubcategories()) {
			putSubcategory(FeedStorageHelper.createHashCode(s), s);
		}
		lastStoredSubcategoriesAmount = storage.getSubcategories().size();
		for (FeedzillaFileArticle a : storage.getArticlesById()) {
			hashArticles.add(FeedStorageHelper.createHashCode(a));
		}
		lastStoredArticlesAmount = hashArticles.size();
		logger.info("Hashcode created. Categories: " + lastStoredCategoriesAmount + ". Subcategories: " + lastStoredSubcategoriesAmount
				+ ". Articles: " + lastStoredArticlesAmount);
		return storage;
	}

	public void save(LocalDateTime daysDownloadFrom) throws FileNotFoundException, IOException {
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
		freeHash(daysDownloadFrom);
		newArticles.clear();
	}

	private void freeHash(LocalDateTime daysDownloadFrom) {
		newArticles.removeIf(new RemoteArticles(daysDownloadFrom, hashArticles));
	}

	private void saveCategories() throws FileNotFoundException, IOException {
		synchronized (hashCategories) {
			FeedzillaFileSaver.saveCategories(feedFolder, hashCategories);
			lastStoredCategoriesAmount = hashCategories.size();
		}
	}

	private void saveSubcategories() throws FileNotFoundException, IOException {
		synchronized (hashSubcategories) {
			FeedzillaFileSaver.saveSubcategories(feedFolder, hashSubcategories);
			lastStoredSubcategoriesAmount = hashSubcategories.size();
		}
	}

	private void saveArticles() throws FileNotFoundException, IOException {
		synchronized (hashArticles) {
			FeedzillaFileSaver.saveArticles(feedFolder, newArticles);
			logger.info("articles saved: " + newArticles.size());
			lastStoredArticlesAmount = hashArticles.size();
		}
	}
	
	public void freeArticles() {
		hashArticles.clear();
	}

	public FeedzillaFileCategory createFeedzillaCategory(FeedzillaFileCategory result) {
		synchronized (hashCategories) {
			final int id = hashCategories.size();
			result.setId(id);
			return putCategory(FeedStorageHelper.createHashCode(result), result);
		}
	}

	private FeedzillaFileCategory putCategory(String hashCode, FeedzillaFileCategory result) {
		final FeedzillaFileCategory oldCategory = hashCategories.putIfAbsent(hashCode, result);
		if (oldCategory != null) {
			return oldCategory;
		} else {
			for (FeedzillaHashStorageReceiver r : receivers) {
				r.addCategory(result);
			}
		}
		return result;
	}

	public FeedzillaFileSubcategory createFeedzillaSubcategory(FeedzillaFileCategory category, FeedzillaFileSubcategory result) {
		synchronized (hashSubcategories) {
			final int id = hashSubcategories.size();
			result.setId(id);
			return putSubcategory(FeedStorageHelper.createHashCode(result), result);
		}
	}

	private FeedzillaFileSubcategory putSubcategory(String hashCode, FeedzillaFileSubcategory result) {
		final FeedzillaFileSubcategory oldSubcategory = hashSubcategories.putIfAbsent(hashCode, result);
		if (oldSubcategory != null) {
			return oldSubcategory;
		} else {
			for (FeedzillaHashStorageReceiver r : receivers) {
				r.addSubCategory(result);
			}
		}
		return result;
	}

	public void createFeedzillaArticle(FeedzillaFileSubcategory subcategory, FeedzillaFileArticle result) {
		synchronized (hashArticles) {
			final int id = hashArticles.size();
			result.setId(id);
			putArticle(FeedStorageHelper.createHashCode(result), result);
		}
	}

	private void putArticle(String hashCode, FeedzillaFileArticle result) {
		if (hashArticles.add(hashCode)) {
			newArticles.add(result);
			for (FeedzillaHashStorageReceiver r : receivers) {
				r.addArticle(result);
			}
		}
	}

	public Map<String, FeedzillaFileCategory> getHashCategories() {
		return hashCategories;
	}

	public Map<String, FeedzillaFileSubcategory> getHashSubcategories() {
		return hashSubcategories;
	}

	@Override
	public boolean addArticle(FeedzillaFileArticle article) {
		if (hashArticles.add(FeedStorageHelper.createHashCode(article))) {
			for (FeedzillaHashStorageReceiver r : receivers) {
				r.addArticle(article);
			}
			return true;
		}
		return false;
	}

}
