package stsc.news.feedzilla.file.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import stsc.common.feeds.FeedCategory;

public class FeedzillaFileCategory implements FeedCategory {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private Integer id;
	private String displayCategoryName;
	private String englishCategoryName;
	private String urlCategoryName;
	private Date createdAt;
	private Date updatedAt;

	public FeedzillaFileCategory(DataInputStream dis) throws IOException {
		this.id = dis.readInt();
		this.displayCategoryName = dis.readUTF();
		this.englishCategoryName = dis.readUTF();
		this.urlCategoryName = dis.readUTF();
		this.createdAt = new Date(dis.readLong());
		this.updatedAt = new Date(dis.readLong());
	}

	public void saveTo(DataOutputStream stream) throws IOException {
		stream.writeInt(id);
		stream.writeUTF(displayCategoryName);
		stream.writeUTF(englishCategoryName);
		stream.writeUTF(urlCategoryName);
		stream.writeLong(createdAt.getTime());
		stream.writeLong(updatedAt.getTime());
	}

	public FeedzillaFileCategory(int id, String displayCategoryName, String englishCategoryName, String urlCategoryName) {
		this.id = id;
		this.displayCategoryName = displayCategoryName;
		this.englishCategoryName = englishCategoryName;
		this.urlCategoryName = urlCategoryName;
		this.createdAt = new Date();
		this.createdAt = new Date();
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String getDisplayCategoryName() {
		return displayCategoryName;
	}

	public void setDisplayCategoryName(String displayCategoryName) {
		this.displayCategoryName = displayCategoryName;
	}

	@Override
	public String getEnglishCategoryName() {
		return englishCategoryName;
	}

	public void setEnglishCategoryName(String englishCategoryName) {
		this.englishCategoryName = englishCategoryName;
	}

	@Override
	public String getUrlCategoryName() {
		return urlCategoryName;
	}

	public void setUrlCategoryName(String urlCategoryName) {
		this.urlCategoryName = urlCategoryName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

}
