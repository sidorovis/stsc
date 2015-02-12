package stsc.database.ormlite.schema;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "articles")
public class FeedzillaOrmliteArticle {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "category_id", canBeNull = false)
	private FeedzillaOrmliteCategory category;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "subcategory_id", canBeNull = false)
	private FeedzillaOrmliteSubcategory subcategory;

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
	private FeedzillaOrmliteArticle() {
		// for ormlite
	}

	public FeedzillaOrmliteArticle(FeedzillaOrmliteSubcategory subcategory, String author, Date publishDate) {
		this.category = subcategory.getCategory();
		this.subcategory = subcategory;
		this.author = author;
		this.publishDate = publishDate;
		this.createdAt = new Date();
		this.updatedAt = new Date();
	}

}
