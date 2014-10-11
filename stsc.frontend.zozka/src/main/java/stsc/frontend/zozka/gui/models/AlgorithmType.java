package stsc.frontend.zozka.gui.models;

public enum AlgorithmType {

	STOCK_VALUE("Stock"), EOD_VALUE("Eod");

	private String value;

	private AlgorithmType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
