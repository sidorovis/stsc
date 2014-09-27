package stsc.frontend.zozka.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class TextAlgorithmParameter {

	private final StringProperty parameterName;
	private final StringProperty type;
	private final StringProperty domen;

	public TextAlgorithmParameter(String parameterName, String type, String domen) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = new SimpleStringProperty(type);
		this.domen = new SimpleStringProperty(domen);
	}

	public StringProperty parameterNameProperty() {
		return parameterName;
	}

	public StringProperty getType() {
		return type;
	}

	public StringProperty domenProperty() {
		return domen;
	}

}
