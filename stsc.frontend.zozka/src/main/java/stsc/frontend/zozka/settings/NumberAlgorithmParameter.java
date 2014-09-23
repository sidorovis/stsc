package stsc.frontend.zozka.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class NumberAlgorithmParameter {

	private final StringProperty parameterName;
	private final StringProperty type;
	private final StringProperty from;
	private final StringProperty step;
	private final StringProperty to;

	public NumberAlgorithmParameter(String parameterName, String type, Double from, Double step, Double to) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = new SimpleStringProperty(type);
		this.from = new SimpleStringProperty("3.0");
		this.step = new SimpleStringProperty("1.0");
		this.to = new SimpleStringProperty("15.0");
	}

	public StringProperty getParameterName() {
		return parameterName;
	}

	public StringProperty getType() {
		return type;
	}

	public StringProperty fromProperty() {
		return from;
	}

	public StringProperty stepProperty() {
		return step;
	}

	public StringProperty toProperty() {
		return to;
	}
}
