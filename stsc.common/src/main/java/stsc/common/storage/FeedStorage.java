package stsc.common.storage;

import java.util.List;

import stsc.common.feeds.FeedArticle;
import stsc.common.feeds.FeedCategory;
import stsc.common.feeds.FeedSubcategory;

public interface FeedStorage {

	// get list methods

	public List<? extends FeedCategory> getCategories();

	public List<? extends FeedSubcategory> getSubcategories();

	public List<? extends FeedArticle> getArticles();

}
