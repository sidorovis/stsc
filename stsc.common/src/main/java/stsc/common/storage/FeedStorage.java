package stsc.common.storage;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import stsc.common.feeds.FeedArticle;
import stsc.common.feeds.FeedCategory;
import stsc.common.feeds.FeedSubcategory;

public interface FeedStorage {

	// get list methods

	public Collection<? extends FeedCategory> getCategories();

	public Collection<? extends FeedSubcategory> getSubcategories();

	public Collection<? extends FeedArticle> getArticles();

	public List<? extends FeedArticle> getArticles(Date publishDate);

}
