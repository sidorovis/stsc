package stsc.news.feedzilla.file.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import stsc.common.feeds.FeedArticle;

public class FeedzillaFileArticle implements FeedArticle {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private Integer id;
	private FeedzillaFileCategory category;
	private FeedzillaFileSubcategory subcategory;
	private String author;
	private LocalDateTime publishDate;
	private String source;
	private String sourceUrl;
	private String summary;
	private String title;
	private String url;
	private Date createdAt;
	private Date updatedAt;

	public FeedzillaFileArticle(DataInputStream dis, Map<Integer, FeedzillaFileSubcategory> subcategories) throws IOException {
		this.id = dis.readInt();
		final int subCategoryId = dis.readInt();
		this.subcategory = subcategories.get(subCategoryId);
		if (subcategory == null) {
			throw new IOException("For article id:" + id + " no subcategory with id: " + subCategoryId);
		}
		this.category = subcategory.getCategory();
		this.author = FileProcessHelper.readNullableUTF(dis);
		this.publishDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(dis.readLong()), ZoneOffset.UTC);
		this.source = FileProcessHelper.readNullableUTF(dis);
		this.sourceUrl = FileProcessHelper.readNullableUTF(dis);
		this.summary = FileProcessHelper.readNullableUTF(dis);
		this.title = FileProcessHelper.readNullableUTF(dis);
		this.url = FileProcessHelper.readNullableUTF(dis);
		this.createdAt = new Date(dis.readLong());
		this.updatedAt = new Date(dis.readLong());
	}

	public void saveTo(DataOutputStream stream) throws IOException {
		stream.writeInt(id);
		stream.writeInt(subcategory.getId());
		FileProcessHelper.writeNullableUTF(stream, author);
		stream.writeLong(publishDate.toInstant(ZoneOffset.UTC).toEpochMilli());
		FileProcessHelper.writeNullableUTF(stream, source);
		FileProcessHelper.writeNullableUTF(stream, sourceUrl);
		FileProcessHelper.writeNullableUTF(stream, summary);
		FileProcessHelper.writeNullableUTF(stream, title);
		FileProcessHelper.writeNullableUTF(stream, url);
		stream.writeLong(createdAt.getTime());
		stream.writeLong(updatedAt.getTime());
	}

	public FeedzillaFileArticle(int id, FeedzillaFileSubcategory subcategory, String author, LocalDateTime publishDate) {
		this.id = id;
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

	public void setId(int id) {
		this.id = id;
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
	public LocalDateTime getPublishDate() {
		return publishDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return "Article: " + getPublishDate().toString();
	}
}
