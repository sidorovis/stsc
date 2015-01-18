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
import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteArticle;
import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteCategory;
import stsc.news.feedzilla.ormlite.schema.FeedzillaOrmliteSubcategory;

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
	private final Dao<FeedzillaOrmliteCategory, Integer> categories;
	private final Dao<FeedzillaOrmliteSubcategory, Integer> subcategories;
	private final Dao<FeedzillaOrmliteArticle, Integer> articles;

	private Map<String, FeedzillaOrmliteCategory> feedzillaCategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaOrmliteSubcategory> feedzillaSubcategories = Collections.synchronizedMap(new HashMap<>());
	private Map<String, FeedzillaOrmliteArticle> feedzillaArticles = Collections.synchronizedMap(new HashMap<>());

	public FeedzillaOrmliteStorage() throws SQLException, IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaOrmliteStorage(final String propertiesFileName) throws SQLException, IOException {
		logger.debug(FeedzillaOrmliteStorage.class + " was loaded from: " + propertiesFileName);
		this.source = getConnectionSource(propertiesFileName);
		this.categories = DaoManager.createDao(source, FeedzillaOrmliteCategory.class);
		this.subcategories = DaoManager.createDao(source, FeedzillaOrmliteSubcategory.class);
		this.articles = DaoManager.createDao(source, FeedzillaOrmliteArticle.class);
		createHashMap();
	}

	private ConnectionSource getConnectionSource(String propertiesFileName) throws IOException, SQLException {
		final FeedzillaDatafeedSettings settings = new FeedzillaDatafeedSettings(propertiesFileName);
		return new JdbcConnectionSource(settings.getJdbcUrl());
	}

	private void createHashMap() {
		logger.info("going to create hash codes");
		final List<FeedzillaOrmliteCategory> categories = getCategories();
		for (FeedzillaOrmliteCategory category : categories) {
			feedzillaCategories.put(createHashCode(category), category);
		}
		final List<FeedzillaOrmliteSubcategory> subcategories = getSubcategories();
		for (FeedzillaOrmliteSubcategory subcategory : subcategories) {
			feedzillaSubcategories.put(createHashCode(subcategory), subcategory);
		}
		final List<FeedzillaOrmliteArticle> articles = getArticles();
		for (FeedzillaOrmliteArticle article : articles) {
			feedzillaArticles.put(createHashCode(article), article);
		}
		logger.info("hashes created: " + feedzillaCategories.size() + " " + feedzillaSubcategories.size() + " " + feedzillaArticles.size());
	}

	public FeedzillaOrmliteCategory update(FeedzillaOrmliteCategory category) {
		final String hashCode = createHashCode(category);
		final FeedzillaOrmliteCategory hashValue = feedzillaCategories.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateCategory(category) > 0) {
			feedzillaCategories.put(hashCode, category);
		}
		return category;
	}

	public FeedzillaOrmliteSubcategory update(FeedzillaOrmliteSubcategory subcategory) {
		final String hashCode = createHashCode(subcategory);
		final FeedzillaOrmliteSubcategory hashValue = feedzillaSubcategories.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateSubcategory(subcategory) > 0) {
			feedzillaSubcategories.put(createHashCode(subcategory), subcategory);
		}
		return subcategory;
	}

	public FeedzillaOrmliteArticle update(FeedzillaOrmliteArticle article) {
		final String hashCode = createHashCode(article);
		final FeedzillaOrmliteArticle hashValue = feedzillaArticles.get(hashCode);
		if (hashValue != null) {
			return hashValue;
		}
		if (createOrUpdateArticle(article) > 0) {
			feedzillaArticles.put(createHashCode(article), article);
		}
		return article;
	}

	private String createHashCode(FeedzillaOrmliteCategory c) {
		return s(c.getDisplayCategoryName()).hashCode() + " " + s(c.getEnglishCategoryName()).hashCode() + " "
				+ s(c.getUrlCategoryName()).hashCode();
	}

	private String createHashCode(FeedzillaOrmliteSubcategory c) {
		return s(c.getDisplaySubcategoryName()).hashCode() + " " + s(c.getEnglishSubcategoryName()).hashCode() + " "
				+ s(c.getUrlSubcategoryName()).hashCode();
	}

	private String createHashCode(FeedzillaOrmliteArticle a) {
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
	public List<FeedzillaOrmliteCategory> getCategories() {
		try {
			return categories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateCategory(FeedzillaOrmliteCategory newCategory) {
		try {
			return categories.createOrUpdate(newCategory).getNumLinesChanged();
		} catch (SQLException e) {
			logger.error("createOrUpdateCategory", e);
			return 0;
		}
	}

	@Override
	public List<FeedzillaOrmliteSubcategory> getSubcategories() {
		try {
			return subcategories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateSubcategory(FeedzillaOrmliteSubcategory newSubcategory) {
		try {
			return subcategories.createOrUpdate(newSubcategory).getNumLinesChanged();
		} catch (SQLException e) {
			logger.error("createOrUpdateSubcategory", e);
			return 0;
		}
	}

	@Override
	public List<FeedzillaOrmliteArticle> getArticles() {
		try {
			return articles.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public int createOrUpdateArticle(FeedzillaOrmliteArticle newArticle) {
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
