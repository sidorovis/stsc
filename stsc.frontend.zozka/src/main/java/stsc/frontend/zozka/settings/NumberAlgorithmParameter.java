package stsc.frontend.zozka.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

final class NumberAlgorithmParameter {
	private final StringProperty parameterName;
	private final StringProperty type;
	private StringProperty from;
	private StringProperty step;
	private StringProperty to;

	// public static NumberAlgorithmParameter getDouble(String parameterName,
	// String type) {
	// return new NumberAlgorithmParameter(parameterName, type, 0.0, 1.0, 5.0);
	// }

	public NumberAlgorithmParameter(String parameterName, String type, Double from, Double step, Double to) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = new SimpleStringProperty(type);
		this.setFrom(new SimpleStringProperty("0.0"));
		this.setStep(new SimpleStringProperty("1.0"));
		this.setTo(new SimpleStringProperty("15.0"));
	}

	public StringProperty getParameterName() {
		return parameterName;
	}

	public StringProperty getType() {
		return type;
	}

	public StringProperty getFrom() {
		return from;
	}

	public void setFrom(StringProperty from) {
		this.from = from;
	}

	public StringProperty getStep() {
		return step;
	}

	public void setStep(StringProperty step) {
		this.step = step;
	}

	public StringProperty getTo() {
		return to;
	}

	public void setTo(StringProperty to) {
		this.to = to;
	}
}
