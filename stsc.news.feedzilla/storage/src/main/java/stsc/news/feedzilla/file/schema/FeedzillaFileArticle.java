package stsc.news.feedzilla.file.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	private Date publishDate;
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
		this.author = dis.readUTF();
		this.publishDate = new Date(dis.readLong());
		this.source = dis.readUTF();
		this.sourceUrl = dis.readUTF();
		this.summary = dis.readUTF();
		this.title = dis.readUTF();
		this.url = dis.readUTF();
		this.createdAt = new Date(dis.readLong());
		this.updatedAt = new Date(dis.readLong());
	}

	public void saveTo(DataOutputStream stream) throws IOException {
		stream.writeInt(id);
		stream.writeInt(subcategory.getId());
		stream.writeUTF(author);
		stream.writeLong(publishDate.getTime());
		stream.writeUTF(source);
		stream.writeUTF(sourceUrl);
		stream.writeUTF(summary);
		stream.writeUTF(title);
		stream.writeUTF(url);
		stream.writeLong(createdAt.getTime());
		stream.writeLong(updatedAt.getTime());
	}

	public FeedzillaFileArticle(int id, FeedzillaFileSubcategory subcategory, String author, Date publishDate) {
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
