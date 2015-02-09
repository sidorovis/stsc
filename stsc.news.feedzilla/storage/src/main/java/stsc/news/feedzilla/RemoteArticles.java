package stsc.news.feedzilla;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;

import stsc.common.feeds.FeedStorageHelper;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;

class RemoteArticles implements Predicate<FeedzillaFileArticle> {

	private LocalDateTime dateBackDownloadFrom;
	private Set<String> hashArticles;

	RemoteArticles(LocalDateTime dateBackDownloadFrom, Set<String> hashArticles) {
		this.dateBackDownloadFrom = dateBackDownloadFrom;
		this.hashArticles = hashArticles;
	}

	@Override
	public boolean test(FeedzillaFileArticle article) {
		if (article.getPublishDate().isBefore(dateBackDownloadFrom)) {
			hashArticles.remove(FeedStorageHelper.createHashCode(article));
			return true;
		}
		return false;
	}
}