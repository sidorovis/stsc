package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.storage.FeedStorage;
import stsc.news.feedzilla.schema.FeedzillaArticle;
import stsc.news.feedzilla.schema.FeedzillaCategory;
import stsc.news.feedzilla.schema.FeedzillaSubcategory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class FeedzillaOrmliteStorage implements FeedStorage {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger(FeedzillaOrmliteStorage.class);

	private final ConnectionSource source;
	private final Dao<FeedzillaCategory, Integer> categories;
	private final Dao<FeedzillaSubcategory, Integer> subcategories;
	private final Dao<FeedzillaArticle, Integer> articles;

	private Map<String, FeedzillaCategory> feedzillaCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaSubcategory> feedzillaSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaArticle> feedzillaArticles = Collections.synchronizedMap(new HashMap<>());

	public FeedzillaOrmliteStorage() throws SQLException, IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaOrmliteStorage(final String propertiesFileName) throws SQLException, IOException {
		logger.debug(FeedzillaOrmliteStorage.class + " was loaded from: " + propertiesFileName);
		this.source = getConnectionSource(propertiesFileName);
		this.categories = DaoManager.createDao(source, FeedzillaCategory.class);
		this.subcategories = DaoManager.createDao(source, FeedzillaSubcategory.class);
		this.articles = DaoManager.createDao(source, FeedzillaArticle.class);
		createHashMap();
		logger.debug("hashes created: " + feedzillaCategories.size() + " " + feedzillaSubcategories.size() + " " + feedzillaArticles.size());
	}

	private ConnectionSource getConnectionSource(String propertiesFileName) throws IOException, SQLException {
		final FeedzillaDatafeedSettings settings = new FeedzillaDatafeedSettings(propertiesFileName);
		return new JdbcConnectionSource(settings.getJdbcUrl());
	}

	private void createHashMap() {
		final List<FeedzillaCategory> categories = getCategories();
		for (FeedzillaCategory category : categories) {
			feedzillaCategories.put(createHashCode(category), category);
		}
		final List<FeedzillaSubcategory> subcategories = getSubcategories();
		for (FeedzillaSubcategory subcategory : subcategories) {
			feedzillaSubcategories.put(createHashCode(subcategory), subcategory);
		}
		final List<FeedzillaArticle> articles = getArticles();
		for (FeedzillaArticle article : articles) {
			feedzillaArticles.put(createHashCode(article), article);
		}
	}

	public FeedzillaCategory update(FeedzillaCategory category) {
		final String hashCode = createHashCode(category);
		final FeedzillaCategory hashValue = feedzillaCategories.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateCategory(category) > 0) {
			feedzillaCategories.put(hashCode, category);
		}
		return category;
	}

	public FeedzillaSubcategory update(FeedzillaSubcategory subcategory) {
		final String hashCode = createHashCode(subcategory);
		final FeedzillaSubcategory hashValue = feedzillaSubcategories.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateSubcategory(subcategory) > 0) {
			feedzillaSubcategories.put(createHashCode(subcategory), subcategory);
		}
		return subcategory;
	}

	public FeedzillaArticle update(FeedzillaArticle article) {
		final String hashCode = createHashCode(article);
		final FeedzillaArticle hashValue = feedzillaArticles.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateArticle(article) > 0) {
			feedzillaArticles.put(createHashCode(article), article);
		}
		return article;
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

	@Override
	public List<FeedzillaCategory> getCategories() {
		try {
			return categories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateCategory(FeedzillaCategory newCategory) {
		try {
			return categories.createOrUpdate(newCategory).getNumLinesChanged();
		} catch (SQLException e) {
			logger.error("createOrUpdateCategory", e);
			return 0;
		}
	}

	@Override
	public List<FeedzillaSubcategory> getSubcategories() {
		try {
			return subcategories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateSubcategory(FeedzillaSubcategory newSubcategory) {
		try {
			return subcategories.createOrUpdate(newSubcategory).getNumLinesChanged();
		} catch (SQLException e) {
			logger.error("createOrUpdateSubcategory", e);
			return 0;
		}
	}

	@Override
	public List<FeedzillaArticle> getArticles() {
		try {
			return articles.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateArticle(FeedzillaArticle newArticle) {
		try {
			return articles.createOrUpdate(newArticle).getNumLinesChanged();
		} catch (SQLException e) {
			logger.error("createOrUpdateArticle", e);
			return 0;
		}
	}

	public void dropAllCategories() {
		try {
			dropAllSubcategories();
			categories.deleteBuilder().delete();
		} catch (SQLException e) {
			// do nothing
		}
	}

	public void dropAllSubcategories() {
		try {
			dropAllArticles();
			subcategories.deleteBuilder().delete();
		} catch (SQLException e) {
			// do nothing
		}
	}

	public void dropAllArticles() {
		try {
			articles.deleteBuilder().delete();
		} catch (SQLException e) {
			// do nothing
		}
	}

}
