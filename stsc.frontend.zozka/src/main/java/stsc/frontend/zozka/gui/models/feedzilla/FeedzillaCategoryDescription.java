package stsc.frontend.zozka.gui.models.feedzilla;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaCategoryDescription {

	final private String id;
	final private String englishCategoryName;
	final private String urlCategoryName;

	public FeedzillaCategoryDescription(final Integer id, final String englishCategoryName, final String urlCategoryName) {
		this.id = (id == null ? "" : String.valueOf(id));
		this.englishCategoryName = (englishCategoryName == null ? "" : englishCategoryName);
		this.urlCategoryName = (urlCategoryName == null ? "" : urlCategoryName);
	}

	public StringProperty idProperty() {
		return new SimpleStringProperty(id);
	}

	public StringProperty nameProperty() {
		return new SimpleStringProperty(englishCategoryName);
	}

	public StringProperty urlProperty() {
		return new SimpleStringProperty(urlCategoryName);
	}
}
