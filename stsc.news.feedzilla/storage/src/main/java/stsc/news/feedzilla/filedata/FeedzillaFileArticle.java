package stsc.news.feedzilla.filedata;

import java.util.Date;

import stsc.common.feeds.FeedArticle;

public class FeedzillaFileArticle implements FeedArticle {

	private Integer id;
	private FeedzillaFileCategory category;
	private FeedzillaFileSubcategory subcategory;
	private String author;
	private Date publishDate;
	private String source;
	private String sourceUrl;
	private String summary;
	private String title;
	private String url;
	private Date createdAt;
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaFileArticle() {
		// for ormlite
	}

	public FeedzillaFileArticle(FeedzillaFileSubcategory subcategory, String author, Date publishDate) {
		this.category = subcategory.getCategory();
		this.subcategory = subcategory;
		this.author = author;
		this.publishDate = publishDate;
		this.createdAt = new Date();
		this.updatedAt = new Date();
	}

	public Integer getId() {
		return id;
	}

	@Override
	public FeedzillaFileCategory getCategory() {
		return category;
	}

	@Override
	public FeedzillaFileSubcategory getSubcategory() {
		return subcategory;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Date getPublishDate() {
		return publishDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
}
