package stsc.frontend.zozka.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.yahoo.liquiditator.StockFilter;

public final class StockDescription {

	private static final StockFilter stockFilter = new StockFilter();

	private final IntegerProperty id;
	private final StringProperty name;
	private final BooleanProperty valid;
	private final BooleanProperty liquid;

	private final Stock stock;

	public StockDescription(int id, UnitedFormatStock stock) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(stock.getName());
		this.valid = new SimpleBooleanProperty(true);
		this.liquid = new SimpleBooleanProperty(stockFilter.test(stock) == null);
		this.stock = stock;
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public BooleanProperty validProperty() {
		return valid;
	}

	public BooleanProperty liquidProperty() {
		return liquid;
	}

	public Stock getStock() {
		return stock;
	}
}