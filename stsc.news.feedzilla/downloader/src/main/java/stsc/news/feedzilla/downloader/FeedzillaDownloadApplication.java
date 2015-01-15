package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.news.feedzilla.FeedzillaStorage;
import stsc.news.feedzilla.schema.FeedzillaArticle;
import stsc.news.feedzilla.schema.FeedzillaCategory;
import stsc.news.feedzilla.schema.FeedzillaSubcategory;

final class FeedzillaDownloadApplication implements LoadFeedReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger(FeedzillaDownloadApplication.class);

	private static String PRODUCTION_FILENAME = "feedzilla_production.properties";
	private static String DEVELOPER_FILENAME = "feedzilla_developer.properties";

	private static FeedzillaDownloadApplication downloadApplication;

	private final FeedzillaStorage feedzillaStorage;
	private final FeedDataDownloader downloader;

	private Map<String, FeedzillaCategory> feedzillaCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaSubcategory> feedzillaSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaArticle> feedzillaArticles = Collections.synchronizedMap(new HashMap<>());

	FeedzillaDownloadApplication() throws SQLException, IOException {
		this(DEVELOPER_FILENAME);
	}

	FeedzillaDownloadApplication(String propertyFile) throws SQLException, IOException {
		this.feedzillaStorage = new FeedzillaStorage(propertyFile);
		this.downloader = new FeedDataDownloader(1, 100);
		createHashMap();
		downloader.addReceiver(this);
	}

	void startDownload() {
		downloader.startDownload();
		for (int i = 200; i > 1; --i) {
			if (downloader.isStopped()) {
				break;
			}
			downloader.setDaysToDownload(i);
			downloader.startDownload();
		}
	}

	private void createHashMap() {
		final List<FeedzillaCategory> categories = feedzillaStorage.getCategories();
		for (FeedzillaCategory category : categories) {
			addCategory(category);
		}
		final List<FeedzillaSubcategory> subcategories = feedzillaStorage.getSubcategories();
		for (FeedzillaSubcategory subcategory : subcategories) {
			addSubcategory(subcategory);
		}
		final List<FeedzillaArticle> articles = feedzillaStorage.getArticles();
		for (FeedzillaArticle article : articles) {
			addArticle(article);
		}
	}

	private String createHashCode(FeedzillaCategory c) {
		return s(c.getDisplayCategoryName()).hashCode() + " " + s(c.getEnglishCategoryName()).hashCode() + " "
				+ s(c.getUrlCategoryName()).hashCode();
	}

	private String createHashCode(FeedzillaSubcategory c) {
		return s(c.getDisplaySubcategoryName()).hashCode() + " " + s(c.getEnglishSubcategoryName()).hashCode() + " "
				+ s(c.getUrlSubcategoryName()).hashCode();
	}

	private String createHashCode(FeedzillaArticle a) {
		return s(a.getAuthor()).hashCode() + " " + s(a.getTitle()).hashCode() + " " + s(a.getPublishDate()) + s(a.getUrl()).hashCode()
				+ " " + s(a.getSummary()).hashCode();
	}

	private static <T> String s(T v) {
		if (v == null) {
			return "null";
		}
		return v.toString();
	}

	private FeedzillaCategory createFeedzillaCategory(Category from) {
		final FeedzillaCategory result = new FeedzillaCategory(from.getDisplayName(), from.getEnglishName(), from.getUrlName());
		if (cacheHave(result)) {
			return feedzillaCategories.get(createHashCode(result));
		}
		return result;
	}

	private FeedzillaSubcategory createFeedzillaSubcategory(FeedzillaCategory categoryFrom, Subcategory from) {
		final FeedzillaSubcategory result = new FeedzillaSubcategory(categoryFrom, from.getDisplayName(), from.getEnglishName(),
				from.getUrlName());
		if (cacheHave(result)) {
			return feedzillaSubcategories.get(createHashCode(result));
		}
		return result;
	}

	private FeedzillaArticle createFeedzillaArticle(FeedzillaSubcategory subcategory, Article from) {
		final FeedzillaArticle to = new FeedzillaArticle(subcategory, from.getAuthor(), from.getPublishDate().toDate());
		to.setSource(from.getSource());
		to.setSourceUrl(from.getSourceUrl());
		to.setSummary(from.getSummary());
		to.setTitle(from.getTitle());
		to.setUrl(from.getUrl());

		if (cacheHave(to)) {
			return feedzillaArticles.get(createHashCode(to));
		}

		return to;
	}

	private void stop() {
		downloader.stopDownload();
	}

	@Override
	public void newArticle(Category newCategory, Subcategory newSubcategory, Article newArticle) {
		final FeedzillaCategory category = createFeedzillaCategory(newCategory);
		final FeedzillaSubcategory subcategory = createFeedzillaSubcategory(category, newSubcategory);
		final FeedzillaArticle article = createFeedzillaArticle(subcategory, newArticle);
		addCategory(category);
		addSubcategory(subcategory);
		addArticle(article);
	}

	private boolean cacheHave(FeedzillaCategory category) {
		final String hashCode = createHashCode(category);
		return feedzillaCategories.containsKey(hashCode);
	}

	private boolean cacheHave(FeedzillaSubcategory subcategory) {
		final String hashCode = createHashCode(subcategory);
		return feedzillaSubcategories.containsKey(hashCode);
	}

	private boolean cacheHave(FeedzillaArticle article) {
		final String hashCode = createHashCode(article);
		return feedzillaArticles.containsKey(hashCode);
	}

	private void addCategory(FeedzillaCategory category) {
		if (cacheHave(category))
			return;
		if (feedzillaStorage.createOrUpdateCategory(category).getNumLinesChanged() != 0) {
			final String hashCode = createHashCode(category);
			feedzillaCategories.put(hashCode, category);
		}
	}

	private void addSubcategory(FeedzillaSubcategory subcategory) {
		if (cacheHave(subcategory))
			return;
		if (feedzillaStorage.createOrUpdateSubcategory(subcategory).getNumLinesChanged() != 0) {
			final String hashCode = createHashCode(subcategory);
			feedzillaSubcategories.put(hashCode, subcategory);
		}
	}

	private void addArticle(FeedzillaArticle article) {
		if (cacheHave(article))
			return;
		if (feedzillaStorage.createOrUpdateArticle(article).getNumLinesChanged() != 0) {
			final String hashCode = createHashCode(article);
			feedzillaArticles.put(hashCode, article);
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
							logger.debug("Started production version");
							downloadApplication = new FeedzillaDownloadApplication(PRODUCTION_FILENAME);
						} else {
							logger.debug("Started developer version");
							downloadApplication = new FeedzillaDownloadApplication(DEVELOPER_FILENAME);
						}
						waitForStarting.countDown();
					} catch (Exception e) {
						logger.error("Error on main execution thread", e);
					}
					downloadApplication.startDownload();
					waitForEnding.countDown();
				}
			});
			mainProcessingThread.start();
			waitForStarting.await();
			logger.debug("Waiting for 'e' - exit command or end of ");
			addExitHook(waitForEnding);
			waitForEnding.await();
			mainProcessingThread.join();
		} catch (Exception e) {
			logger.error("Error on main function. ", e);
		}
	}

	private static void addExitHook(final CountDownLatch waitForEnding) {
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
		} catch (IOException e) {
			logger.error("Error on exit hook. ", e);
			downloadApplication.stop();
		}
	}
}
