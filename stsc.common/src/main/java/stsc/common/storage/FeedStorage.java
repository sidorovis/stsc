package stsc.common.storage;

import stsc.common.feeds.Feed;

public interface FeedStorage {

	public Feed getFeed(String feedName);

}
