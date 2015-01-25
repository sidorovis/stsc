package stsc.frontend.zozka.gui.models.feedzilla;

import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaArticleDescription {

	final private SimpleStringProperty publishDate;

	public FeedzillaArticleDescription(String publishDate) {
		this.publishDate = new SimpleStringProperty(publishDate.toString());
	}

	public StringProperty dateProperty() {
		return publishDate;
	}
}
