package stsc.news.feedzilla;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.storage.FeedStorage;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaFileStorage implements FeedStorage<FeedzillaFileArticle> {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedzillaFileStorage.class);

	private final String feedFolder;
	private final LocalDateTime dateBackDownloadFrom;
	private final boolean storeFeed;
	private final List<FeedzillaFileStorageReceiver> receivers = new ArrayList<>();

	private final Map<Integer, FeedzillaFileCategory> categories = new ConcurrentHashMap<>();
	private final Map<Integer, FeedzillaFileSubcategory> subcategories = new ConcurrentHashMap<>();
	private final List<FeedzillaFileArticle> articles = Collections.synchronizedList(new ArrayList<>());
	private final Map<LocalDateTime, List<FeedzillaFileArticle>> articlesByDate = Collections.synchronizedMap(new TreeMap<>());

	public FeedzillaFileStorage(String feedFolder, LocalDateTime dateBackDownloadFrom, boolean storeFeed) {
		this.feedFolder = feedFolder;
		this.dateBackDownloadFrom = dateBackDownloadFrom;
		this.storeFeed = storeFeed;
	}

	public void addReceiver(FeedzillaFileStorageReceiver r) {
		receivers.add(r);
	}

	public void readData() throws FileNotFoundException, IOException {
		readCategories();
		readSubcategories();
		readArticles();
	}

	private void readCategories() throws FileNotFoundException, IOException {
		final File file = new File(feedFolder + "/" + "_categories" + FeedzillaFileSaver.FILE_EXTENSION);
		if (file.exists()) {
			try (DataInputStream f = new DataInputStream(new FileInputStream(feedFolder + "/" + "_categories"
					+ FeedzillaFileSaver.FILE_EXTENSION))) {
				final long sizeOfCategories = f.readLong();
				for (long i = 0; i < sizeOfCategories; ++i) {
					final FeedzillaFileCategory category = new FeedzillaFileCategory(f);
					categories.put(category.getId(), category);
				}
			}
		}
	}

	private void readSubcategories() throws FileNotFoundException, IOException {
		final File file = new File(feedFolder + "/" + "_subcategories" + FeedzillaFileSaver.FILE_EXTENSION);
		if (file.exists()) {
			try (DataInputStream f = new DataInputStream(new FileInputStream(feedFolder + "/" + "_subcategories"
					+ FeedzillaFileSaver.FILE_EXTENSION))) {
				final long sizeOfSubcategories = f.readLong();
				for (long i = 0; i < sizeOfSubcategories; ++i) {
					final FeedzillaFileSubcategory subcategory = new FeedzillaFileSubcategory(f, categories);
					subcategories.put(subcategory.getId(), subcategory);
				}
			}
		}
	}

	private void readArticles() throws FileNotFoundException, IOException {
		final List<String> articleNames = readFileList(feedFolder);
		for (String articleName : articleNames) {
			final String filePath = feedFolder + "/" + articleName + FeedzillaFileSaver.FILE_ARTICLE_EXTENSION;
			try (DataInputStream f = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {
				final long sizeOfArticles = f.readLong();
				readArticleAndProcess(f, articleName, sizeOfArticles);
			}
		}
	}

	private void readArticleAndProcess(DataInputStream f, String articleName, long sizeOfArticles) throws IOException {
		logger.info("We are going to load: " + articleName + "(" + sizeOfArticles + ")");
		int realLoadedArticles = 0;
		int newArticleId = 0;
		for (long i = 0; i < sizeOfArticles; ++i) {
			final FeedzillaFileArticle article = new FeedzillaFileArticle(f, subcategories);
			article.setId(newArticleId++);
			if (checkArticlePublishDate(article)) {
				boolean addArticle = false;
				for (FeedzillaFileStorageReceiver r : receivers) {
					if (r.addArticle(article)) {
						addArticle = true;
					}
				}
				if (addArticle) {
					realLoadedArticles += 1;
					storeFeed(article);
				}
			}
		}
		logger.info("We actually loaded: " + realLoadedArticles);
	}

	private void storeFeed(final FeedzillaFileArticle article) {
		if (storeFeed) {
			articles.add(article);
			final List<FeedzillaFileArticle> list = articlesByDate.get(article.getPublishDate());
			if (list != null) {
				list.add(article);
			} else {
				final List<FeedzillaFileArticle> newList = Collections.synchronizedList(new ArrayList<>());
				newList.add(article);
				articlesByDate.put(article.getPublishDate(), newList);
			}
		}
	}

	private boolean checkArticlePublishDate(FeedzillaFileArticle article) {
		return dateBackDownloadFrom.isBefore(article.getPublishDate());
	}

	public static List<String> readFileList(String feedFolder) {
		final File folder = new File(feedFolder);
		final File[] listOfFiles = folder.listFiles();
		final List<String> fileNames = new ArrayList<>();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(FeedzillaFileSaver.FILE_ARTICLE_EXTENSION)) {
				fileNames.add(filename.substring(0, filename.length() - FeedzillaFileSaver.FILE_ARTICLE_EXTENSION.length()));
			}
		}
		return fileNames;
	}

	@Override
	public Collection<FeedzillaFileCategory> getCategories() {
		return categories.values();
	}

	@Override
	public Collection<FeedzillaFileSubcategory> getSubcategories() {
		return subcategories.values();
	}

	@Override
	public Collection<FeedzillaFileArticle> getArticlesById() {
		return articles;
	}

	@Override
	public Map<LocalDateTime, List<FeedzillaFileArticle>> getArticlesByDate() {
		return articlesByDate;
	}

	@Override
	public List<FeedzillaFileArticle> getArticles(LocalDateTime publishDate) {
		final List<FeedzillaFileArticle> result = articlesByDate.get(publishDate);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}
}
