package stsc.frontend.zozka.gui.models.feedzilla;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaArticleDescription {

	final private int index;
	final private LocalDateTime publishDate;

	public FeedzillaArticleDescription(final int index, LocalDateTime publishDate) {
		this.index = index;
		this.publishDate = publishDate;
	}

	public StringProperty dateProperty() {
		return new SimpleStringProperty("" + index + " " + publishDate);
	}
}
