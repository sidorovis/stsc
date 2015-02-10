package stsc.frontend.zozka.gui.models.feedzilla;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaSubcategoryDescription {

	final private String id;
	final private String englishSubcategoryName;
	final private String urlSubcategoryName;
	final private String categoryName;

	public FeedzillaSubcategoryDescription(final Integer id, final String englishCategoryName, final String urlCategoryName,
			final String categoryName) {
		this.id = (id == null ? "" : String.valueOf(id));
		this.englishSubcategoryName = (englishCategoryName == null ? "" : englishCategoryName);
		this.urlSubcategoryName = (urlCategoryName == null ? "" : urlCategoryName);
		this.categoryName = (categoryName == null ? "" : categoryName);
	}

	public StringProperty idProperty() {
		return new SimpleStringProperty(id);
	}

	public StringProperty nameProperty() {
		return new SimpleStringProperty(englishSubcategoryName);
	}

	public StringProperty urlProperty() {
		return new SimpleStringProperty(urlSubcategoryName);
	}

	public StringProperty categoryNameProperty() {
		return new SimpleStringProperty(categoryName);
	}
}
