package stsc.news.feedzilla.filedata;

import java.util.Date;

import stsc.common.feeds.FeedSubcategory;

public class FeedzillaFileSubcategory implements FeedSubcategory {

	private Integer id;
	private FeedzillaFileCategory category;
	private String displaySubcategoryName;
	private String englishSubcategoryName;
	private String urlSubcategoryName;
	private Date createdAt;
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaFileSubcategory() {
		// for ormlite
	}

	public FeedzillaFileSubcategory(FeedzillaFileCategory category, String displayCategoryName, String englishCategoryName,
			String urlCategoryName) {
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
