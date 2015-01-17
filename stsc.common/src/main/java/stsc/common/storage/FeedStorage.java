package stsc.common.storage;

import java.util.List;

import stsc.common.feeds.FeedArticle;
import stsc.common.feeds.FeedCategory;
import stsc.common.feeds.FeedSubcategory;

public interface FeedStorage {

	// create or update methods
	//
	// public <T extends FeedCategory> int createOrUpdateCategory(T
	// newCategory);
	//
	// public int createOrUpdateSubcategory(FeedSubcategory newSubcategory);
	//
	// public int createOrUpdateArticle(FeedArticle newArticle);

	// get list methods

	public List<? extends FeedCategory> getCategories();

	public List<? extends FeedSubcategory> getSubcategories();

	public List<? extends FeedArticle> getArticles();

}
