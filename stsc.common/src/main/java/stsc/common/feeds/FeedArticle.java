package stsc.common.feeds;

import java.time.LocalDateTime;

public interface FeedArticle {

	public FeedCategory getCategory();

	public FeedSubcategory getSubcategory();

	public String getAuthor();

	public String getSource();

	public String getSourceUrl();

	public String getSummary();

	public String getTitle();

	public String getUrl();

	public LocalDateTime getPublishDate();

}
