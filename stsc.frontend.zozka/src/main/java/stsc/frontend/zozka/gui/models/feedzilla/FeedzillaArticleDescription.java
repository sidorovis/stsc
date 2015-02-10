package stsc.frontend.zozka.gui.models.feedzilla;

import java.time.LocalDateTime;

import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaArticleDescription {

	final private String author;
	final private String title;
	final private LocalDateTime publishDate;
	final private String url;
	final private String subcategoryName;
	final private String categoryName;

	public FeedzillaArticleDescription(final FeedzillaFileArticle article) {
		this(article.getAuthor(), article.getTitle(), article.getPublishDate(), article.getSourceUrl(), article.getSubcategory()
				.getEnglishSubcategoryName(), article.getSubcategory().getCategory().getEnglishCategoryName());
	}

	public FeedzillaArticleDescription(final String author, final String title, LocalDateTime publishDate, String url,
			String subcategoryName, String categoryName) {
		this.author = (author == null ? "" : author);
		this.title = (title == null ? "" : title);
		this.publishDate = publishDate;
		this.url = (url == null ? "" : url);
		this.subcategoryName = (subcategoryName == null ? "" : subcategoryName);
		this.categoryName = (categoryName == null ? "" : categoryName);
	}

	public StringProperty dateProperty() {
		return new SimpleStringProperty("" + publishDate);
	}

	public StringProperty authorProperty() {
		return new SimpleStringProperty(author);
	}

	public StringProperty titleProperty() {
		return new SimpleStringProperty(title);
	}

	public StringProperty urlProperty() {
		return new SimpleStringProperty(url);
	}

	public StringProperty subcategoryNameProperty() {
		return new SimpleStringProperty(subcategoryName);
	}

	public StringProperty categoryNameProperty() {
		return new SimpleStringProperty(categoryName);
	}
}
