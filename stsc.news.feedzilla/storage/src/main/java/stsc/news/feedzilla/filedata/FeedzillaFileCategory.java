package stsc.news.feedzilla.filedata;

import java.util.Date;

import stsc.common.feeds.FeedCategory;

public class FeedzillaFileCategory implements FeedCategory {

	private Integer id;
	private String displayCategoryName;
	private String englishCategoryName;
	private String urlCategoryName;
	private Date createdAt;
	private Date updatedAt;

	@SuppressWarnings("unused")
	private FeedzillaFileCategory() {
		// for ormlite
	}

	public FeedzillaFileCategory(String displayCategoryName, String englishCategoryName, String urlCategoryName) {
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
