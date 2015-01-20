package stsc.news.feedzilla.file.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import stsc.common.feeds.FeedSubcategory;

public class FeedzillaFileSubcategory implements FeedSubcategory {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private Integer id;
	private FeedzillaFileCategory category;
	private String displaySubcategoryName;
	private String englishSubcategoryName;
	private String urlSubcategoryName;
	private Date createdAt;
	private Date updatedAt;

	public FeedzillaFileSubcategory(DataInputStream dis, Map<Integer, FeedzillaFileCategory> categories) throws IOException {
		this.id = dis.readInt();
		final int categoryId = dis.readInt();
		this.category = categories.get(categoryId);
		if (category == null) {
			throw new IOException("For subcategory id:" + id + " no category with id: " + categoryId);
		}
		this.displaySubcategoryName = FileProcessHelper.readNullableUTF(dis);
		this.englishSubcategoryName = FileProcessHelper.readNullableUTF(dis);
		this.urlSubcategoryName = FileProcessHelper.readNullableUTF(dis);
		this.createdAt = new Date(dis.readLong());
		this.updatedAt = new Date(dis.readLong());
	}

	public void saveTo(DataOutputStream stream) throws IOException {
		stream.writeInt(id);
		stream.writeInt(category.getId());
		FileProcessHelper.writeNullableUTF(stream, displaySubcategoryName);
		FileProcessHelper.writeNullableUTF(stream, englishSubcategoryName);
		FileProcessHelper.writeNullableUTF(stream, urlSubcategoryName);
		stream.writeLong(createdAt.getTime());
		stream.writeLong(updatedAt.getTime());
	}

	public FeedzillaFileSubcategory(int id, FeedzillaFileCategory category, String displayCategoryName, String englishCategoryName,
			String urlCategoryName) {
		this.id = id;
		this.category = category;
		this.displaySubcategoryName = displayCategoryName;
		this.englishSubcategoryName = englishCategoryName;
		this.urlSubcategoryName = urlCategoryName;
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
	public String getDisplaySubcategoryName() {
		return displaySubcategoryName;
	}

	public void setDisplaySubcategoryName(String displaySubcategoryName) {
		this.displaySubcategoryName = displaySubcategoryName;
	}

	@Override
	public String getEnglishSubcategoryName() {
		return englishSubcategoryName;
	}

	public void setEnglishSubcategoryName(String englishSubcategoryName) {
		this.englishSubcategoryName = englishSubcategoryName;
	}

	@Override
	public String getUrlSubcategoryName() {
		return urlSubcategoryName;
	}

	public void setUrlSubcategoryName(String urlSubcategoryName) {
		this.urlSubcategoryName = urlSubcategoryName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

}
