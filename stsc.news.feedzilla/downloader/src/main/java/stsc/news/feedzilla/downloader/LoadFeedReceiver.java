package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

public interface LoadFeedReceiver {
	public void newArticle(Category category, Subcategory subcategory, Article article);
}
