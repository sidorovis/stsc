package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import stsc.common.storage.FeedStorage;
import stsc.news.feedzilla.schema.FeedZillaArticle;
import stsc.news.feedzilla.schema.FeedZillaCategory;
import stsc.news.feedzilla.schema.FeedZillaSubcategory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class FeedzillaStorage implements FeedStorage {

	// private static Logger logger =
	// LogManager.getLogger(FeedzillaFileStorage.class);
	// private static final Logger

	private final ConnectionSource source;
	private final Dao<FeedZillaCategory, Integer> categories;
	private final Dao<FeedZillaSubcategory, Integer> subcategories;
	private final Dao<FeedZillaArticle, Integer> articles;

	public FeedzillaStorage() throws SQLException, IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaStorage(String propertiesFileName) throws SQLException, IOException {
		this.source = getConnectionSource(propertiesFileName);
		this.categories = DaoManager.createDao(source, FeedZillaCategory.class);
		this.subcategories = DaoManager.createDao(source, FeedZillaSubcategory.class);
		this.articles = DaoManager.createDao(source, FeedZillaArticle.class);
	}

	private ConnectionSource getConnectionSource(String propertiesFileName) throws IOException, SQLException {
		final FeedzillaDatafeedSettings settings = new FeedzillaDatafeedSettings(propertiesFileName);
		return new JdbcConnectionSource(settings.getJdbcUrl());
	}

	public List<FeedZillaCategory> getCategories() {
		try {
			return categories.queryBuilder().orderBy("id", true).query();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public CreateOrUpdateStatus createOrUpdateCategory(FeedZillaCategory newCategory) {
		try {
			return categories.createOrUpdate(newCategory);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	public CreateOrUpdateStatus createOrUpdateSubcategory(FeedZillaSubcategory newSubcategory) {
		try {
			return subcategories.createOrUpdate(newSubcategory);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	public CreateOrUpdateStatus createOrUpdateArticle(FeedZillaArticle newArticle) {
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
	//
	// public void addReceiver(LoadFeedReceiver receiver) {
	// receivers.add(receiver);
	// }
	//
	// private void updateReceivers(final Feed feed) {
	// for (LoadFeedReceiver loadFeedReceiver : receivers) {
	// loadFeedReceiver.newFeed(feed);
	// }
	// }
	//
	// public void addFeed(final Feed newFeed) {
	// // final DateTime publishDate = newFeed.getArticle().getPublishDate();
	// // final List<Feed> feedList = datafeed.get(publishDate);
	// // synchronized (datafeed) {
	// // if (feedList == null) {
	// // final List<Feed> newFeedList = Collections.synchronizedList(new
	// // ArrayList<>());
	// // newFeedList.add(newFeed);
	// // datafeed.put(publishDate, newFeedList);
	// // } else {
	// // feedList.add(newFeed);
	// // }
	// // }
	// }
	//
	// @Override
	// public List<Feed> getFeeds(final DateTime dateTime) {
	// return datafeed.get(dateTime);
	// }

}
