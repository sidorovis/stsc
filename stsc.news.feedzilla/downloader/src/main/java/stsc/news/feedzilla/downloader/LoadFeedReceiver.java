package stsc.news.feedzilla.downloader;

public interface LoadFeedReceiver<FeedCategoryType, FeedSubcategoryType, FeedArticleType> {

	public void newFeedCategory(FeedCategoryType feedCategoryType);

	public void newFeedSubcategory(FeedSubcategoryType feedSubcategoryType);

	public void newFeedArticle(FeedArticleType feedArticleType);
}
