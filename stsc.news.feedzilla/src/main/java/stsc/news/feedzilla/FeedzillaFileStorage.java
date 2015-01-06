package stsc.news.feedzilla;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import stsc.common.feeds.Feed;
import stsc.common.storage.FeedStorage;

public class FeedzillaFileStorage implements FeedStorage {

	private Map<DateTime, List<Feed>> datafeed = Collections.synchronizedMap(new HashMap<>());

	private final List<LoadFeedReceiver> receivers = Collections.synchronizedList(new ArrayList<>());

	public FeedzillaFileStorage(String feedFolder) {
		readFeedzillaData();
	}

	private void readFeedzillaData() {
		// TODO Auto-generated method stub

	}

	public void addReceiver(LoadFeedReceiver receiver) {
		receivers.add(receiver);
	}

	private void updateReceivers(final Feed feed) {
		for (LoadFeedReceiver loadFeedReceiver : receivers) {
			loadFeedReceiver.newFeed(feed);
		}
	}

	public void addFeed(final Feed newFeed) {
		final DateTime publishDate = newFeed.getArticle().getPublishDate();
		final List<Feed> feedList = datafeed.get(publishDate);
		synchronized (datafeed) {
			if (feedList == null) {
				final List<Feed> newFeedList = Collections.synchronizedList(new ArrayList<>());
				newFeedList.add(newFeed);
				datafeed.put(publishDate, newFeedList);
			} else {
				feedList.add(newFeed);
			}
		}
	}

	@Override
	public List<Feed> getFeeds(final DateTime dateTime) {
		return datafeed.get(dateTime);
	}

}
