package stsc.news.feedzilla;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import stsc.news.feedzilla.schema.FeedZillaCategory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class FeedzillaFileStorage {

//	private static final Logger 
	
	private final ConnectionSource source;
	private final Dao<FeedZillaCategory, Integer> categories;

	public FeedzillaFileStorage() throws SQLException, IOException {
		this("feedzilla_developer.properties");
	}

	public FeedzillaFileStorage(String propertiesFileName) throws SQLException, IOException {
		this.source = getConnectionSource(propertiesFileName);
		this.categories = DaoManager.createDao(source, FeedZillaCategory.class);
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

	public CreateOrUpdateStatus addCategory(FeedZillaCategory newCategory) {
		try {
			return categories.createOrUpdate(newCategory);
		} catch (SQLException e) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	public void dropAllCategories() {
		try {
			categories.deleteBuilder().delete();
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
