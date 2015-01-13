package stsc.news.feedzilla.schema;

import java.util.Date;

import stsc.common.feeds.FeedArticle;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "articles")
public class FeedzillaArticle implements FeedArticle {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "category_id", canBeNull = false)
	private FeedzillaCategory category;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "subcategory_id", canBeNull = false)
	private FeedzillaSubcategory subcategory;

	@DatabaseField(columnName = "author", useGetSet = true)
	private String author;

	@DatabaseField(columnName = "publish_date", dataType = DataType.DATE_STRING)
	private Date publishDate;

	@DatabaseField(columnName = "source", useGetSet = true)
	private String source;

	@DatabaseField(columnName = "source_url", useGetSet = true)
	private String sourceUrl;

	@DatabaseField(columnName = "summary", useGetSet = true)
	private String summary;

	@DatabaseField(columnName = "title", useGetSet = true)
	private String title;

	@DatabaseField(columnName = "url", useGetSet = true)
	private String url;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE_STRING)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE_STRING)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaArticle() {
		// for ormlite
	}

	public FeedzillaArticle(FeedzillaSubcategory subcategory, String author, Date publishDate) {
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
	public FeedzillaCategory getCategory() {
		return category;
	}

	@Override
	public FeedzillaSubcategory getSubcategory() {
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
