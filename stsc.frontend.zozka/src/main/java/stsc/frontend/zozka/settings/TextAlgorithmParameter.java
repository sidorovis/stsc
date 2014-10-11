package stsc.frontend.zozka.settings;

import java.util.ArrayList;
import java.util.List;

import stsc.frontend.zozka.gui.models.ParameterType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class TextAlgorithmParameter {

	private final StringProperty parameterName;
	private final ParameterType type;
	private final StringProperty domen;

	public TextAlgorithmParameter(String parameterName, ParameterType type, List<String> domen) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = type;
		this.domen = new SimpleStringProperty(createStringRepresentation(domen));
	}

	public TextAlgorithmParameter(String parameterName, ParameterType type, String domen) {
		this.parameterName = new SimpleStringProperty(parameterName);
		this.type = type;
		this.domen = new SimpleStringProperty(domen);
	}

	public StringProperty parameterNameProperty() {
		return parameterName;
	}

	public String getType() {
		return type.getName();
	}

	public StringProperty domenProperty() {
		return domen;
	}

	public static String createStringRepresentation(List<String> values) {
		String domen = "'";
		for (int i = 0; i < values.size(); ++i) {
			domen += values.get(i);
			if (i < values.size() - 1) {
				domen += "', '";
			}
		}
		domen += "'";
		return domen;
	}

	public static List<String> createDomenRepresentation(String string) {
		List<String> domen = new ArrayList<>();
		for (String p : string.split(",")) {
			final String trimmed = p.trim();
			if (trimmed.length() >= 2 && trimmed.charAt(0) == '\'' && trimmed.charAt(trimmed.length() - 1) == '\'') {
				domen.add(trimmed.substring(1, trimmed.length() - 1));
			}
		}
		return domen;
	}

}
