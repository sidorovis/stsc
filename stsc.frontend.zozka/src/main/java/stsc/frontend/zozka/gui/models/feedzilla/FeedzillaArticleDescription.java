package stsc.frontend.zozka.gui.models.feedzilla;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaArticleDescription {

	final private String author;
	final private String title;
	final private LocalDateTime publishDate;

	public FeedzillaArticleDescription(final String author, final String title, LocalDateTime publishDate) {
		this.author = (author == null ? "" : author);
		this.title = (title == null ? "" : title);
		this.publishDate = publishDate;
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
}
