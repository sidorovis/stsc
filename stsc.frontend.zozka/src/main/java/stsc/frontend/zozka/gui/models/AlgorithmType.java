package stsc.frontend.zozka.gui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum AlgorithmType {

	STOCK_VALUE("Stock"), EOD_VALUE("Eod");

	private String value;

	private AlgorithmType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static ObservableList<AlgorithmType> getObservableList() {
		return FXCollections.observableArrayList(AlgorithmType.values());
	}

	public boolean isStock() {
		return this.equals(STOCK_VALUE);
	}

	@Override
	public String toString() {
		return value;
	}
}
