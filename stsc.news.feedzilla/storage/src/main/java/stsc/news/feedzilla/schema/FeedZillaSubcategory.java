package stsc.news.feedzilla.schema;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "subcategories")
public class FeedZillaSubcategory {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "category_id", canBeNull = false)
	private FeedZillaCategory category;

	@DatabaseField(columnName = "display_subcategory_name", useGetSet = true)
	private String displayCategoryName;

	@DatabaseField(columnName = "english_subcategory_name", useGetSet = true)
	private String englishCategoryName;

	@DatabaseField(columnName = "url_subcategory_name", useGetSet = true)
	private String urlCategoryName;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE_STRING)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE_STRING)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedZillaSubcategory() {
		// for ormlite
	}

	public FeedZillaSubcategory(FeedZillaCategory category, String displayCategoryName, String englishCategoryName, String urlCategoryName) {
		this.category = category;
		this.displayCategoryName = displayCategoryName;
		this.englishCategoryName = englishCategoryName;
		this.urlCategoryName = urlCategoryName;
		this.createdAt = new Date();
		this.updatedAt = new Date();
	}

	public Integer getId() {
		return id;
	}

	public FeedZillaCategory getCategory() {
		return category;
	}

	public String getDisplayCategoryName() {
		return displayCategoryName;
	}

	public void setDisplayCategoryName(String displayCategoryName) {
		this.displayCategoryName = displayCategoryName;
	}

	public String getEnglishCategoryName() {
		return englishCategoryName;
	}

	public void setEnglishCategoryName(String englishCategoryName) {
		this.englishCategoryName = englishCategoryName;
	}

	public String getUrlCategoryName() {
		return urlCategoryName;
	}

	public void setUrlCategoryName(String urlCategoryName) {
		this.urlCategoryName = urlCategoryName;
	}

}
