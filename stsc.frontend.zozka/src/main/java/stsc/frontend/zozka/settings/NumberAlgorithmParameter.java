package stsc.frontend.zozka.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class NumberAlgorithmParameter {

	private final StringProperty parameterName;
	private final String type;
	private final StringProperty from;
	private final StringProperty step;
	private final StringProperty to;
	private boolean valid;

	public NumberAlgorithmParameter(String parameterName, String type, String from, String step, String to) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = new String(type);
		this.from = new SimpleStringProperty(from);
		this.step = new SimpleStringProperty(step);
		this.to = new SimpleStringProperty(to);
		this.valid = true;
	}

	public StringProperty parameterNameProperty() {
		return parameterName;
	}

	public String getType() {
		return type;
	}

	public String getFrom() {
		return from.getValue();
	}

	public void setFrom(String value) {
		from.setValue(value);
		validate();
	}

	public String getStep() {
		return step.getValue();
	}

	public void setStep(String value) {
		step.setValue(value);
		validate();
	}

	public String getTo() {
		return to.getValue();
	}

	public void setTo(String value) {
		to.setValue(value);
		validate();
	}

	public boolean isValid() {
		return valid;
	}

	private void validate() {
		try {
			valid = false;

			double fromValue = Double.valueOf(from.getValue());
			Double.valueOf(step.getValue());
			double toValue = Double.valueOf(to.getValue());

			if (fromValue > toValue) {
				return;
			}

			valid = true;
		} catch (NumberFormatException e) {
			valid = false;
		}
		// if (from > to) {
		// valid = false;
		// return;
		// }
	}
}
