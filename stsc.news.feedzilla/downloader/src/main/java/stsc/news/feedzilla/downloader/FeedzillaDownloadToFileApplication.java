package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.feeds.FeedStorageHelper;
import stsc.news.feedzilla.FeedzillaFileStorage;
import stsc.news.feedzilla.filedata.FeedzillaFileArticle;
import stsc.news.feedzilla.filedata.FeedzillaFileCategory;
import stsc.news.feedzilla.filedata.FeedzillaFileSubcategory;

final class FeedzillaDownloadToFileApplication implements LoadFeedReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger logger = LogManager.getLogger(FeedzillaDownloadToFileApplication.class);

	private static String PRODUCTION_FILENAME = "feedzilla_production.properties";
	private static String DEVELOPER_FILENAME = "feedzilla_developer.properties";

	private static FeedzillaDownloadToFileApplication downloadApplication;

	private final FeedDataDownloader downloader;

	private final String feedFolder;

	private Map<String, FeedzillaFileCategory> hashCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaFileSubcategory> hashSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaFileArticle> hashArticles = Collections.synchronizedMap(new HashMap<>());

	private int lastStoredCategoriesAmount = 0;
	private int lastStoredSubcategoriesAmount = 0;
	private int lastStoredArticlesAmount = 0;
	private List<FeedzillaFileArticle> newArticles = Collections.synchronizedList(new ArrayList<>());

	FeedzillaDownloadToFileApplication() throws SQLException, IOException {
		this(DEVELOPER_FILENAME);
	}

	FeedzillaDownloadToFileApplication(String propertyFile) throws IOException {
		this.feedFolder = readFeedFolderProperty(propertyFile);
		this.downloader = new FeedDataDownloader(1, 100);
		if (feedFolder == null) {
			throw new IOException("There is no setting 'feed.folder' at property file: " + propertyFile);
		}
		readFeedData();
		downloader.addReceiver(this);
	}

	private String readFeedFolderProperty(String propertyFile) throws FileNotFoundException, IOException {
		try (DataInputStream inputStream = new DataInputStream(new FileInputStream("./config/" + propertyFile))) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			return properties.getProperty("feed.folder");
		}
	}

	private void readFeedData() throws FileNotFoundException, IOException {
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
	}

	void startDownload() throws FileNotFoundException, IOException {
		downloadAndSave();
		for (int i = 3650; i > 1; --i) {
			if (downloader.isStopped()) {
				break;
			}
			downloader.setDaysToDownload(i);
			downloadAndSave();
		}
	}

	private void downloadAndSave() throws FileNotFoundException, IOException {
		downloader.download();
		if (hashCategories.size() != lastStoredCategoriesAmount) {
			saveCategories();
		}
		if (hashSubcategories.size() != lastStoredSubcategoriesAmount) {
			saveSubcategories();
		}
		if (hashArticles.size() != lastStoredArticlesAmount) {
			saveArticles();
		}
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
			lastStoredArticlesAmount = hashArticles.size();
		}
	}

	private void stop() throws InterruptedException {
		downloader.stopDownload();
	}

	@Override
	public void newArticle(Category newCategory, Subcategory newSubcategory, Article newArticle) {
		final FeedzillaFileCategory category = createFeedzillaCategory(newCategory);
		final FeedzillaFileSubcategory subcategory = createFeedzillaSubcategory(category, newSubcategory);
		createFeedzillaArticle(subcategory, newArticle);
	}

	private FeedzillaFileCategory createFeedzillaCategory(Category from) {
		synchronized (hashCategories) {
			final int id = hashCategories.size();
			final FeedzillaFileCategory result = new FeedzillaFileCategory(id, from.getDisplayName(), from.getEnglishName(),
					from.getUrlName());
			final String hashCode = FeedStorageHelper.createHashCode(result);
			final FeedzillaFileCategory oldCategory = hashCategories.putIfAbsent(hashCode, result);
			if (oldCategory != null) {
				return oldCategory;
			}
			return result;
		}
	}

	private FeedzillaFileSubcategory createFeedzillaSubcategory(FeedzillaFileCategory category, Subcategory from) {
		synchronized (hashSubcategories) {
			final int id = hashSubcategories.size();
			final FeedzillaFileSubcategory result = new FeedzillaFileSubcategory(id, category, from.getDisplayName(),
					from.getEnglishName(), from.getUrlName());
			final String hashCode = FeedStorageHelper.createHashCode(result);
			final FeedzillaFileSubcategory oldSubcategory = hashSubcategories.putIfAbsent(hashCode, result);
			if (oldSubcategory != null) {
				return oldSubcategory;
			}
			return result;
		}
	}

	private void createFeedzillaArticle(FeedzillaFileSubcategory subcategory, Article from) {
		synchronized (hashArticles) {
			final int id = hashArticles.size();
			final FeedzillaFileArticle result = new FeedzillaFileArticle(id, subcategory, from.getAuthor(), from.getPublishDate().toDate());
			result.setSource(from.getSource());
			result.setSourceUrl(from.getSourceUrl());
			result.setSummary(from.getSummary());
			result.setTitle(from.getTitle());
			result.setUrl(from.getUrl());

			final String hashCode = FeedStorageHelper.createHashCode(result);
			if (hashArticles.putIfAbsent(hashCode, result) == null) {
				newArticles.add(result);
			}
		}
	}

	public static void main(String[] args) {
		final CountDownLatch waitForStarting = new CountDownLatch(1);
		final CountDownLatch waitForEnding = new CountDownLatch(1);
		try {
			final Thread mainProcessingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (args.length > 0 && args[0] == "production") {
							logger.info("Started production version");
							downloadApplication = new FeedzillaDownloadToFileApplication(PRODUCTION_FILENAME);
						} else {
							logger.info("Started developer version");
							downloadApplication = new FeedzillaDownloadToFileApplication(DEVELOPER_FILENAME);
						}
						waitForStarting.countDown();
						downloadApplication.startDownload();
					} catch (Exception e) {
						logger.error("Error on main execution thread", e);
					}
					waitForEnding.countDown();
				}
			});
			mainProcessingThread.start();
			waitForStarting.await();
			logger.info("Please enter 'e' and press Enter to stop application.");
			addExitHook(waitForEnding);
			waitForEnding.await();
			mainProcessingThread.join();
		} catch (Exception e) {
			logger.error("Error on main function. ", e);
		}
	}

	private static void addExitHook(final CountDownLatch waitForEnding) {
		try {
			try {
				final InputStreamReader fileInputStream = new InputStreamReader(System.in);
				final BufferedReader bufferedReader = new BufferedReader(fileInputStream);

				while (true) {
					if (bufferedReader.ready()) {
						final String s = bufferedReader.readLine();
						if (s.equals("e")) {
							downloadApplication.stop();
							break;
						}
					}
					if (waitForEnding.getCount() == 0) {
						downloadApplication.stop();
						break;
					}
				}
				bufferedReader.close();
			} catch (Exception e) {
				logger.error("Error on exit hook. ", e);
				downloadApplication.stop();
			}
		} catch (Exception e) {
			logger.error("Error on exit hook with non stop. ", e);
		}
	}
}
