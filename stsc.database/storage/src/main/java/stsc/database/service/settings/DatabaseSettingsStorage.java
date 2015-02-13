package stsc.database.service.settings;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.Validate;

import stsc.database.migrations.DatabaseSettings;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseSettingsStorage {

	private final ConnectionSource source;

	private final Dao<OrmliteYahooDatafeedSettings, Integer> yahooDatafeedSettings;

	public DatabaseSettingsStorage(final DatabaseSettings databaseSettings) throws IOException, SQLException {
		System.out.println(new File("./").getAbsoluteFile());
		this.source = getConnectionSource(databaseSettings);
		this.yahooDatafeedSettings = DaoManager.createDao(source, OrmliteYahooDatafeedSettings.class);
		Validate.isTrue(yahooDatafeedSettings.isTableExists(), "OrmliteYahooDatafeedSettings table should exists");
	}

	public ConnectionSource getConnectionSource(final DatabaseSettings databaseSettings) throws IOException, SQLException {
		return new JdbcConnectionSource(databaseSettings.getJdbcUrl());
	}

	public CreateOrUpdateStatus setYahooDatafeedSettings(OrmliteYahooDatafeedSettings newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return yahooDatafeedSettings.createOrUpdate(newCategory);
	}

	public OrmliteYahooDatafeedSettings getYahooDatafeedSettings(String settingName) throws SQLException {
		return yahooDatafeedSettings.queryForEq(OrmliteYahooDatafeedSettings.settingColumnName, settingName).get(0);
	}

	// private final ConnectionSource source;
	// private final Dao<FeedzillaOrmliteCategory, Integer> categories;
	// private final Dao<FeedzillaOrmliteSubcategory, Integer> subcategories;
	// private final Dao<FeedzillaOrmliteArticle, Integer> articles;
	//
	// public FeedzillaOrmliteStorage(final String propertiesFileName) throws
	// SQLException, IOException {
	// logger.debug(FeedzillaOrmliteStorage.class + " was loaded from: " +
	// propertiesFileName);
	// this.source = getConnectionSource(propertiesFileName);
	// this.categories = DaoManager.createDao(source,
	// FeedzillaOrmliteCategory.class);
	// this.subcategories = DaoManager.createDao(source,
	// FeedzillaOrmliteSubcategory.class);
	// this.articles = DaoManager.createDao(source,
	// FeedzillaOrmliteArticle.class);
	// createHashMap();
	// }
	//
	//
	// private void createHashMap() {
	// logger.info("going to create hash codes");
	// final Collection<FeedzillaOrmliteCategory> categories = getCategories();
	// for (FeedzillaOrmliteCategory category : categories) {
	// feedzillaCategories.put(FeedStorageHelper.createHashCode(category),
	// category);
	// }
	// final Collection<FeedzillaOrmliteSubcategory> subcategories =
	// getSubcategories();
	// for (FeedzillaOrmliteSubcategory subcategory : subcategories) {
	// feedzillaSubcategories.put(FeedStorageHelper.createHashCode(subcategory),
	// subcategory);
	// }
	// final Collection<FeedzillaOrmliteArticle> articles = getArticles();
	// for (FeedzillaOrmliteArticle article : articles) {
	// feedzillaArticles.put(FeedStorageHelper.createHashCode(article),
	// article);
	// final List<FeedzillaOrmliteArticle> articleList =
	// articlesByDate.get(article.getPublishDate());
	// if (articleList == null) {
	// final List<FeedzillaOrmliteArticle> newArticleList =
	// Collections.synchronizedList(new ArrayList<>());
	// articlesByDate.put(article.getPublishDate(), newArticleList);
	// } else {
	// articleList.add(article);
	// }
	// }
	// logger.info("hashes created: " + feedzillaCategories.size() + " " +
	// feedzillaSubcategories.size() + " " + feedzillaArticles.size());
	// }
	//
	// public FeedzillaOrmliteCategory update(FeedzillaOrmliteCategory category)
	// {
	// final String hashCode = FeedStorageHelper.createHashCode(category);
	// final FeedzillaOrmliteCategory hashValue =
	// feedzillaCategories.get(hashCode);
	// if (hashValue != null) {
	// return hashValue;
	// }
	// if (createOrUpdateCategory(category) > 0) {
	// feedzillaCategories.put(hashCode, category);
	// }
	// return category;
	// }
	//
	// public FeedzillaOrmliteSubcategory update(FeedzillaOrmliteSubcategory
	// subcategory) {
	// final String hashCode = FeedStorageHelper.createHashCode(subcategory);
	// final FeedzillaOrmliteSubcategory hashValue =
	// feedzillaSubcategories.get(hashCode);
	// if (hashValue != null) {
	// return hashValue;
	// }
	// if (createOrUpdateSubcategory(subcategory) > 0) {
	// feedzillaSubcategories.put(hashCode, subcategory);
	// }
	// return subcategory;
	// }
	//
	// public FeedzillaOrmliteArticle update(FeedzillaOrmliteArticle article) {
	// final String hashCode = FeedStorageHelper.createHashCode(article);
	// final FeedzillaOrmliteArticle hashValue =
	// feedzillaArticles.get(hashCode);
	// if (hashValue != null) {
	// return hashValue;
	// }
	// if (createOrUpdateArticle(article) > 0) {
	// feedzillaArticles.put(hashCode, article);
	// }
	// return article;
	// }
	//
	// @Override
	// public Collection<FeedzillaOrmliteCategory> getCategories() {
	// try {
	// return categories.queryBuilder().orderBy("id", true).query();
	// } catch (SQLException e) {
	// return Collections.emptyList();
	// }
	// }
	//
	// public int createOrUpdateCategory(FeedzillaOrmliteCategory newCategory) {
	// try {
	// return categories.createOrUpdate(newCategory).getNumLinesChanged();
	// } catch (SQLException e) {
	// logger.error("createOrUpdateCategory", e);
	// return 0;
	// }
	// }
	//
	// @Override
	// public Collection<FeedzillaOrmliteSubcategory> getSubcategories() {
	// try {
	// return subcategories.queryBuilder().orderBy("id", true).query();
	// } catch (SQLException e) {
	// return Collections.emptyList();
	// }
	// }
	//
	// public int createOrUpdateSubcategory(FeedzillaOrmliteSubcategory
	// newSubcategory) {
	// try {
	// return subcategories.createOrUpdate(newSubcategory).getNumLinesChanged();
	// } catch (SQLException e) {
	// logger.error("createOrUpdateSubcategory", e);
	// return 0;
	// }
	// }
	//
	// @Override
	// public Collection<FeedzillaOrmliteArticle> getArticles() {
	// try {
	// return articles.queryBuilder().orderBy("id", true).query();
	// } catch (SQLException e) {
	// return Collections.emptyList();
	// }
	// }
	//
	// @Override
	// public List<FeedzillaOrmliteArticle> getArticles(final Date publishDate)
	// {
	// final List<FeedzillaOrmliteArticle> articlesToReturn =
	// articlesByDate.get(publishDate);
	// if (articlesToReturn == null) {
	// return Collections.emptyList();
	// }
	// return articlesToReturn;
	// }
	//
	// public int createOrUpdateArticle(FeedzillaOrmliteArticle newArticle) {
	// try {
	// return articles.createOrUpdate(newArticle).getNumLinesChanged();
	// } catch (SQLException e) {
	// logger.error("createOrUpdateArticle", e);
	// return 0;
	// }
	// }
	//
	// public void dropAllCategories() {
	// try {
	// dropAllSubcategories();
	// categories.deleteBuilder().delete();
	// } catch (SQLException e) {
	// // do nothing
	// }
	// }
	//
	// public void dropAllSubcategories() {
	// try {
	// dropAllArticles();
	// subcategories.deleteBuilder().delete();
	// } catch (SQLException e) {
	// // do nothing
	// }
	// }
	//
	// public void dropAllArticles() {
	// try {
	// articles.deleteBuilder().delete();
	// } catch (SQLException e) {
	// // do nothing
	// }
	// }
	//
}
