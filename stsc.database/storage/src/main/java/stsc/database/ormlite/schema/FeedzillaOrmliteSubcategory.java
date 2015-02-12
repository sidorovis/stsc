package stsc.news.feedzilla.ormlite.schema;

import java.util.Date;

import stsc.common.feeds.FeedSubcategory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "subcategories")
public class FeedzillaOrmliteSubcategory implements FeedSubcategory {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "category_id", canBeNull = false)
	private FeedzillaOrmliteCategory category;

	@DatabaseField(columnName = "display_subcategory_name", useGetSet = true)
	private String displaySubcategoryName;

	@DatabaseField(columnName = "english_subcategory_name", useGetSet = true)
	private String englishSubcategoryName;

	@DatabaseField(columnName = "url_subcategory_name", useGetSet = true)
	private String urlSubcategoryName;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE_STRING)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE_STRING)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaOrmliteSubcategory() {
		// for ormlite
	}

	public FeedzillaOrmliteSubcategory(FeedzillaOrmliteCategory category, String displayCategoryName, String englishCategoryName, String urlCategoryName) {
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

	@Override
	public FeedzillaOrmliteCategory getCategory() {
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

}
