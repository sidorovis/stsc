package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Category;
import graef.feedzillajava.Subcategory;

interface LoadFeedReceiver {

	public void newArticle(Category category, Subcategory subcategory, Article article);

}
