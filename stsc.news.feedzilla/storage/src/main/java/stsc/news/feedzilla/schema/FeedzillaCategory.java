package stsc.news.feedzilla.schema;

import java.util.Date;

import stsc.common.feeds.FeedCategory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "categories")
public class FeedzillaCategory implements FeedCategory {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = "display_category_name", useGetSet = true)
	private String displayCategoryName;

	@DatabaseField(columnName = "english_category_name", useGetSet = true)
	private String englishCategoryName;

	@DatabaseField(columnName = "url_category_name", useGetSet = true)
	private String urlCategoryName;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE_STRING)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE_STRING)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaCategory() {
		// for ormlite
	}

	public FeedzillaCategory(String displayCategoryName, String englishCategoryName, String urlCategoryName) {
		this.displayCategoryName = displayCategoryName;
		this.englishCategoryName = englishCategoryName;
		this.urlCategoryName = urlCategoryName;
		this.createdAt = new Date();
		this.updatedAt = new Date();
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

}
