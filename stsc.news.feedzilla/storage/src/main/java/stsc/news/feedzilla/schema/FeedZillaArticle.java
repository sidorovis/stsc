package stsc.news.feedzilla.schema;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "articles")
public class FeedZillaArticle {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "category_id", canBeNull = false)
	private FeedZillaCategory category;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "subcategory_id", canBeNull = false)
	private FeedZillaSubcategory subcategory;

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
	private FeedZillaArticle() {
		// for ormlite
	}

	public FeedZillaArticle(FeedZillaSubcategory subcategory, String author, Date publishDate) {
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FeedZillaCategory getCategory() {
		return category;
	}

	public FeedZillaSubcategory getSubcategory() {
		return subcategory;
	}

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
