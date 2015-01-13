package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.storage.FeedStorage;
import stsc.news.feedzilla.schema.FeedzillaArticle;
import stsc.news.feedzilla.schema.FeedzillaCategory;
import stsc.news.feedzilla.schema.FeedzillaSubcategory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class FeedzillaStorage implements FeedStorage {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger(FeedzillaStorage.class);

	private final ConnectionSource source;
	private final Dao<FeedzillaCategory, Integer> categories;
	private final Dao<FeedzillaSubcategory, Integer> subcategories;
	private final Dao<FeedzillaArticle, Integer> articles;

	public FeedzillaStorage() throws SQLException, IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaStorage(final String propertiesFileName) throws SQLException, IOException {
		logger.debug(FeedzillaStorage.class + " was loaded from: " + propertiesFileName);
		this.source = getConnectionSource(propertiesFileName);
		this.categories = DaoManager.createDao(source, FeedzillaCategory.class);
		this.subcategories = DaoManager.createDao(source, FeedzillaSubcategory.class);
		this.articles = DaoManager.createDao(source, FeedzillaArticle.class);
	}

	private ConnectionSource getConnectionSource(String propertiesFileName) throws IOException, SQLException {
		final FeedzillaDatafeedSettings settings = new FeedzillaDatafeedSettings(propertiesFileName);
		return new JdbcConnectionSource(settings.getJdbcUrl());
	}

	public List<FeedzillaCategory> getCategories() {
		try {
			return categories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public CreateOrUpdateStatus createOrUpdateCategory(FeedzillaCategory newCategory) {
		try {
			return categories.createOrUpdate(newCategory);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	public List<FeedzillaSubcategory> getSubcategories() {
		try {
			return subcategories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public CreateOrUpdateStatus createOrUpdateSubcategory(FeedzillaSubcategory newSubcategory) {
		try {
			return subcategories.createOrUpdate(newSubcategory);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	public List<FeedzillaArticle> getArticles() {
		try {
			return articles.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public CreateOrUpdateStatus createOrUpdateArticle(FeedzillaArticle newArticle) {
		try {
			return articles.createOrUpdate(newArticle);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
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
