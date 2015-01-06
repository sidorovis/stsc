package stsc.common.storage;

import java.util.List;

import org.joda.time.DateTime;

import stsc.common.feeds.Feed;

public interface FeedStorage {

	public List<Feed> getFeeds(final DateTime dateTime);

}
