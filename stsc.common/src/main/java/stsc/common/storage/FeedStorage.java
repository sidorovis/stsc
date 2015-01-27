package stsc.common.storage;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import stsc.common.feeds.FeedArticle;
import stsc.common.feeds.FeedCategory;
import stsc.common.feeds.FeedSubcategory;

public interface FeedStorage<T extends FeedArticle> {

	// get list methods

	public Collection<? extends FeedCategory> getCategories();

	public Collection<? extends FeedSubcategory> getSubcategories();

	public Collection<T> getArticlesById();

	public Map<Date, List<T>> getArticlesByDate();

	public List<T> getArticles(Date publishDate);

}
